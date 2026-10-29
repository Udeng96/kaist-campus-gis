package com.eseict.ibsmodule.service.event;

import com.eseict.ibsmodule.data.cache.FireSensorCache;
import com.eseict.ibsmodule.data.repository.*;
import com.eseict.ibsmodule.data.vo.*;
import com.eseict.ibsmodule.websocket.WebSocket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.eseict.ibsmodule.config.ApiConstant.EVENT_TYPE;

@RequiredArgsConstructor
@Component
@Slf4j
public class EventParserService implements ParserHandler {

    private static final Logger fireEventLogger = LoggerFactory.getLogger("eventLogger");

    private final WebSocket ws;
    private final FireSensorCache fireSensorCache;

    private final SensorRepository sensorRepository;
    private final EventRepository eventRepository;
    private final LogRepository logRepository;
    private final CctvRepository cctvRepository;
    private final EventCctvRepository eventCctvRepository;

    private static final int EVENT_ON = 1;

    private Object getBuildingLock(Long buildingId) {
        return ("FIRE_EVENT_" + buildingId).intern();
    }

    @Override
    public void handleAllEvent(OffsetDateTime occurredAt, String panel) {
        log.info("[ALL EVENT CLEAR] - PANEL::{}", panel);
        fireEventLogger.info("[ALL EVENT CLEAR] - PANEL::{}", panel);

        List<FireSensor> sensorsByPanel = sensorRepository.findByPanel(panel);

        Map<Long, List<FireSensor>> byBuilding = sensorsByPanel.stream()
                .filter(s -> s.getBuildingId() != null)
                .collect(Collectors.groupingBy(FireSensor::getBuildingId));

        for (Long buildingId : byBuilding.keySet()) {
            synchronized (getBuildingLock(buildingId)) {
                List<String> activeSensorIds = fireSensorCache.getActiveSensorIds(buildingId);
                List<String> panelSensorIds = byBuilding.get(buildingId).stream()
                        .map(FireSensor::getSensorId).collect(Collectors.toList());

                Set<String> remainActiveSensors = new HashSet<>(activeSensorIds);
                remainActiveSensors.removeAll(panelSensorIds);

                for (String sensorId : panelSensorIds) {
                    fireSensorCache.updateSensorStatus(buildingId, sensorId, false);
                }

                if (remainActiveSensors.isEmpty()) {
                    List<Event> events = findActiveEvents(buildingId);
                    if (!events.isEmpty()) {
                        Event event = events.get(0);
                        eventRepository.updateClearedAt(occurredAt, event.getId());
                        event.setClearedAt(occurredAt);
                        ws.sendToWeb(event);
                        log.info("건물(ID:{})의 화재 이벤트가 종료됩니다.", buildingId);
                    }
                }
            }
        }
        fireEventLogger.info("{} 수신기 10번(전체 종료) 타입 발생", panel);
        log.info("{} 수신기 10번(전체 종료) 타입 발생", panel);
    }

    public void handleOnFire(OffsetDateTime occurredAt, String sensorId, Long buildingId) {
        synchronized (getBuildingLock(buildingId)) {
            List<Event> events = findActiveEvents(buildingId);

            Long eventId;
            if (events.isEmpty()) {
                // 이벤트 생성
                Event newEvent = new Event(EVENT_TYPE, buildingId, sensorId, occurredAt);
                newEvent = eventRepository.save(newEvent);
                eventId = newEvent.getId();

                // CCTV 매핑
                List<Cctv> cctvs = cctvRepository.findByBuildingId(buildingId);
                for (Cctv cctv : cctvs) {
                    eventCctvRepository.save(new EventCctv(eventId, cctv.getCctvId()));
                }

                ws.sendToWeb(newEvent);
            } else {
                eventId = events.get(0).getId();
            }

            saveLog(eventId, sensorId, buildingId, 1, occurredAt);
            fireSensorCache.updateSensorStatus(buildingId, sensorId, true);
            log.info("건물(ID:{}) {} 센서 화재 감지 ::: {}개의 센서 발생 중",
                    buildingId, sensorId, fireSensorCache.countActiveSensors(buildingId));
        }
    }

    public void handleOffFire(OffsetDateTime occurredAt, String sensorId, Long buildingId) {
        synchronized (getBuildingLock(buildingId)) {
            List<Event> events = findActiveEvents(buildingId);

            fireSensorCache.updateSensorStatus(buildingId, sensorId, false);
            int lastActiveSensorCnt = fireSensorCache.countActiveSensors(buildingId);

            // 이벤트가 있으면 해당 eventId로 로그 저장, 없으면 로그 생략
            if (!events.isEmpty()) {
                Long eventId = events.get(0).getId();
                saveLog(eventId, sensorId, buildingId, 0, occurredAt);

                if (lastActiveSensorCnt == 0) {
                    eventRepository.updateClearedAt(occurredAt, eventId);
                    events.get(0).setClearedAt(occurredAt);
                    ws.sendToWeb(events.get(0));
                    log.info("건물(ID:{})의 화재 이벤트가 해제되었습니다.", buildingId);
                } else {
                    log.info("건물(ID:{})의 {}개의 센서가 아직 해제되지 않았습니다.", buildingId, lastActiveSensorCnt);
                }
            }
        }
    }

    @Override
    public void handleFireEvent(OffsetDateTime occurredAt, String panel, String area, String device, byte[] mapAddr, int lOn) {
        String mapAddrStr = new String(mapAddr, StandardCharsets.US_ASCII).replace("\0", "").trim();

        int subPanel = 0, loopNo = 0, repeaterNo = 0, circuitNo = 0;

        if (mapAddrStr.length() >= 7) {
            subPanel = Integer.parseInt(mapAddrStr.substring(0, 2));
            loopNo = mapAddrStr.charAt(2) - '0';
            repeaterNo = Integer.parseInt(mapAddrStr.substring(3, 6));
            circuitNo = mapAddrStr.charAt(6) - '0';
        } else {
            log.info("잘못된 mapAddr:{}", mapAddrStr);
        }

        String rawId = panel + String.format("%02d", subPanel) + loopNo + String.format("%03d", repeaterNo) + circuitNo;
        String sensorId = rawId.replaceAll("[^0-9A-Za-z]", "");

        log.info("[FIRE EVENT] - lON::{}, PANEL::{}, SUB_PANEL::{}, LOOP::{}, Repeater::{}, Circuit::{}, AREA::{}, DEVICE::{}",
                lOn, panel, subPanel, loopNo, repeaterNo, circuitNo, area, device);
        fireEventLogger.info("[FIRE EVENT] - lON::{}, PANEL::{}, SUB_PANEL::{}, LOOP::{}, Repeater::{}, Circuit::{}, AREA::{}, DEVICE::{}",
                lOn, panel, subPanel, loopNo, repeaterNo, circuitNo, area, device);

        Optional<FireSensor> sensorInfo = sensorRepository.findBySensorId(sensorId);

        if (sensorInfo.isPresent()) {
            FireSensor sensor = sensorInfo.get();
            Long buildingId = sensor.getBuildingId();

            if (buildingId == null) {
                log.warn("센서 {}에 buildingId가 매핑되어 있지 않습니다.", sensorId);
                return;
            }

            if (lOn == EVENT_ON) {
                handleOnFire(occurredAt, sensor.getSensorId(), buildingId);
            } else {
                handleOffFire(occurredAt, sensor.getSensorId(), buildingId);
            }
        } else {
            log.info("{}센서 정보가 존재하지 않습니다.", sensorId);
        }
    }

    private void saveLog(Long eventId, String sensorId, Long buildingId, int status, OffsetDateTime detectedAt) {
        FireLog fireLog = new FireLog(eventId, sensorId, buildingId, status, detectedAt);
        logRepository.save(fireLog);
    }

    private List<Event> findActiveEvents(Long buildingId) {
        return eventRepository.findActiveByTypeAndBuildingId(EVENT_TYPE, buildingId);
    }
}
