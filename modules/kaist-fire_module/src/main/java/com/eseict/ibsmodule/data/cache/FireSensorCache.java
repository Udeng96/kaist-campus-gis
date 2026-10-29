package com.eseict.ibsmodule.data.cache;

import com.eseict.ibsmodule.data.repository.LogRepository;
import com.eseict.ibsmodule.data.repository.SensorRepository;
import com.eseict.ibsmodule.data.vo.FireLog;
import com.eseict.ibsmodule.data.vo.FireSensor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class FireSensorCache {

    private final SensorRepository sensorRepository;
    private final LogRepository logRepository;

    /**
     * 건물별 센서 상태 캐시
     * key: buildingId (Long), value: {sensorId -> active여부}
     */
    private final Map<Long, Map<String, Boolean>> buildingSensorCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void initCache() {
        log.info("init cache");

        List<FireSensor> allSensors = sensorRepository.findAll();

        Map<String, FireLog> latestLogBySensor = logRepository.findLatestLogPerSensor()
                .stream()
                .collect(Collectors.toMap(FireLog::getSensorId, l -> l, (a, b) -> a));

        Map<Long, List<FireSensor>> byBuilding = allSensors.stream()
                .filter(s -> s.getBuildingId() != null)
                .collect(Collectors.groupingBy(FireSensor::getBuildingId));

        for (Map.Entry<Long, List<FireSensor>> entry : byBuilding.entrySet()) {
            Long buildingId = entry.getKey();
            int eventCnt = 0;

            Map<String, Boolean> sensorMap = new ConcurrentHashMap<>();
            for (FireSensor sensor : entry.getValue()) {
                FireLog lastLog = latestLogBySensor.get(sensor.getSensorId());
                boolean isActive = lastLog != null && lastLog.getStatus() == 1;
                sensorMap.put(sensor.getSensorId(), isActive);
                if (isActive) eventCnt++;
            }
            buildingSensorCache.put(buildingId, sensorMap);
            log.info("건물(ID:{})에서 이벤트가 발생중인 센서 : {}", buildingId, eventCnt);
        }
        log.info("센서 캐시 초기화 완료: {}개 건물, 총 {}개 센서", buildingSensorCache.size(), allSensors.size());
    }

    public void updateSensorStatus(Long buildingId, String sensorId, boolean isActive) {
        Map<String, Boolean> sensors = buildingSensorCache.computeIfAbsent(buildingId, k -> new ConcurrentHashMap<>());
        sensors.put(sensorId, isActive);
    }

    public List<String> getActiveSensorIds(Long buildingId) {
        Map<String, Boolean> sensors = buildingSensorCache.get(buildingId);
        if (sensors == null) return Collections.emptyList();
        List<String> sensorIds = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : sensors.entrySet()) {
            if (Boolean.TRUE.equals(entry.getValue())) {
                sensorIds.add(entry.getKey());
            }
        }
        return sensorIds;
    }

    public int countActiveSensors(Long buildingId) {
        Map<String, Boolean> sensors = buildingSensorCache.get(buildingId);
        if (sensors == null) return 0;
        int count = 0;
        for (Boolean state : sensors.values()) {
            if (state) count++;
        }
        return count;
    }

    public int getTotalSensors(Long buildingId) {
        Map<String, Boolean> sensors = buildingSensorCache.get(buildingId);
        return (sensors != null) ? sensors.size() : 0;
    }
}
