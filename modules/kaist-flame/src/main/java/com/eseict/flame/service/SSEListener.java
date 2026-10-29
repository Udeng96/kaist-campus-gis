package com.eseict.flame.service;


import com.eseict.flame.data.dto.FireEventDto;
import com.eseict.flame.data.vo.event.TblEventHist;
import com.eseict.flame.data.vo.event.TblFlameLog;
import com.eseict.flame.data.vo.fac.TblCctv;
import com.eseict.flame.repository.CctvRepository;
import com.eseict.flame.repository.EventRepository;
import com.eseict.flame.repository.LogRepository;
import com.eseict.flame.websocket.WebSocket;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class SSEListener {


    private final WebSocket ws;

    private final EventRepository eventRepository;
    private final LogRepository logRepository;
    private final CctvRepository cctvRepository;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter OUT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private final ObjectMapper mapper = new ObjectMapper();


    @Value("${sse.host}")
    private String sseHost;

    @Value("${sse.port}")
    private int ssePort;

    @Value("${sse.contextPath}")
    private String sseContextPath;

    @Value("${sse.timeout}") // 기본값 15000ms
    private long timeoutMillis;

    @Value("${sse.reconnect-interval}") // 기본값 5000ms
    private long reconnectInterval;

    // 마지막 이벤트 수신 시각 (epoch millis)
    private final AtomicLong lastEventTime = new AtomicLong(System.currentTimeMillis());


    // 현재 연결 보관
    private volatile HttpURLConnection currentConnection;

    // 불꽃 이벤트 버스트 처리용 큐 (FIFO 순차 처리)
    private final BlockingQueue<FireEventDto> flameQueue = new LinkedBlockingQueue<>();
    // 이미 미해제 이벤트가 있는 건물 (인메모리 스킵셋). 자정에 clear.
    private final Set<String> activeFlameBuildings = ConcurrentHashMap.newKeySet();


    @PostConstruct
    public void init() {
        // 구동 초기화: 미해제 불꽃 이벤트를 건물별 최신 1건만 남기고 나머지 해제 + 활성 건물 셋 구성
        initActiveFlameBuildings();

        // 연결 스레드
        Thread connectThread = new Thread(this::connectToSse, "SSE-Connect-Thread");
        connectThread.setDaemon(true);
        connectThread.start();

        // watchdog 스레드
        Thread watchdogThread = new Thread(this::watchdog, "SSE-Watchdog-Thread");
        watchdogThread.setDaemon(true);
        watchdogThread.start();

        // 불꽃 이벤트 워커 스레드 (큐 순차 소비)
        Thread flameWorkerThread = new Thread(this::flameWorker, "SSE-Flame-Worker");
        flameWorkerThread.setDaemon(true);
        flameWorkerThread.start();
    }

    private void connectToSse() {
        while (true) {
            try {
                log.info(" SSE 연결 시도: {}:{}{}", sseHost, ssePort, sseContextPath);

                URL url = new URL("http://" + sseHost + ":" + ssePort + sseContextPath);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "text/event-stream");
                conn.setRequestProperty("Cache-Control", "no-cache");

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    currentConnection = conn;
                    lastEventTime.set(System.currentTimeMillis()); // ✅ 연결 성공 시 초기화
                    log.info(" SSE 연결 성공: {}:{}{}", sseHost, ssePort, sseContextPath);
                } else {
                    log.warn(" SSE 연결 실패 (응답 코드: {})", responseCode);
                    Thread.sleep(reconnectInterval);
                    continue;
                }
                int zeroCnt = 0;

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("data:")) {
                            String json = line.substring(5).trim();
                            FireEventDto dto = mapper.readValue(json, FireEventDto.class);

                            //  이벤트 수신 시마다 갱신
                            lastEventTime.set(System.currentTimeMillis());

                            if (dto.getType() == 0) {
                                if (zeroCnt == 50) {
                                    log.info(" [전체] 내용::{}", line);
                                    zeroCnt = 0;
                                } else {
                                    zeroCnt = zeroCnt + 1;
                                }
                                continue;
                            }

                            if (dto.getType() == 37) {
                                // 버스트 대비: 적재만 하고 실제 처리는 워커가 순차로 (맨 처음 것부터)
                                flameQueue.offer(dto);
                            } else {
                                log.debug("keep-alive 이벤트 수신");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("SSE 연결 끊김. {}ms 후 재연결 시도", reconnectInterval, e);
                try {
                    Thread.sleep(reconnectInterval);
                } catch (InterruptedException ignored) {
                }
            } finally {
                if (currentConnection != null) {
                    currentConnection.disconnect();
                    currentConnection = null;
                }
            }
        }
    }

    private void watchdog() {
        while (true) {
            try {
                if (currentConnection == null) {
                    Thread.sleep(reconnectInterval);
                    continue;
                }

                long now = System.currentTimeMillis();
                if (now - lastEventTime.get() > timeoutMillis) {
                    log.error(" {}ms 동안 이벤트 없음 → SSE 연결 강제 종료 후 재연결 시도", timeoutMillis);
                    try {
                        currentConnection.disconnect();
                    } catch (Exception e) {
                        log.warn("연결 종료 중 오류 발생", e);
                    }
                    currentConnection = null; // 다음 루프에서 connectToSse()가 재연결 수행
                }

                Thread.sleep(reconnectInterval);
            } catch (InterruptedException ignored) {
            }
        }
    }

    // 구동 시: 미해제 불꽃 이벤트를 건물별로 최신 1건만 남기고 나머지는 해제. 남긴 건물은 활성 셋에 등록.
    private void initActiveFlameBuildings() {
        List<TblEventHist> unresolved = eventRepository.findByTypeAndClrDtm("flame", "");
        Map<String, List<TblEventHist>> byBuilding = unresolved.stream()
                .collect(Collectors.groupingBy(TblEventHist::getMappBuildingId));

        String clrDtm = LocalDateTime.now(KST).format(OUT);
        int resolved = 0;
        for (Map.Entry<String, List<TblEventHist>> entry : byBuilding.entrySet()) {
            List<TblEventHist> list = entry.getValue();
            // outb_dtm 내림차순 → 첫 번째(최신)만 남기고 이전 것들은 해제
            list.sort(Comparator.comparing(TblEventHist::getOutbDtm).reversed());
            for (int i = 1; i < list.size(); i++) {
                TblEventHist old = list.get(i);
                old.setClrDtm(clrDtm);
                eventRepository.save(old);
                resolved++;
            }
            activeFlameBuildings.add(entry.getKey()); // 최신 1건 유지 → 활성 건물
        }
        log.info("[구동 초기화] 미해제 건물 {}곳, 이전 중복 {}건 해제, 활성 건물 셋 구성", byBuilding.size(), resolved);
    }

    // 큐에서 불꽃 이벤트를 하나씩 순차 소비 (FIFO)
    private void flameWorker() {
        while (true) {
            try {
                FireEventDto dto = flameQueue.take();
                processFlameEvent(dto);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (Exception e) {
                log.error("불꽃 이벤트 처리 중 오류", e);
            }
        }
    }

    private void processFlameEvent(FireEventDto dto) {
        String outbDtm = formatEpochAuto(dto.getTimestamp());
        int dev = dto.getDevices().get(0);
        String streamId = "CCTV04" + String.format("%03d", dev);

        TblCctv cctvInfo = cctvRepository.findByPlcTypeAndStreamId("3", streamId);
        if (cctvInfo == null) {
            log.info(" [없는 CCTV 정보 이벤트] streamId{}", streamId);
            return;
        }
        String buildingCd = cctvInfo.getBuilding(); // 불꽃 감지 이벤트는 무조건 1:1 매핑
        String today = LocalDateTime.now(KST).format(OUT).substring(0, 8);

        // 1) flame_log : 센서당 하루 1건 (화면 전송 X)
        if (!logRepository.existsBySensorIdAndInsertDtmStartingWith(streamId, today)) {
            String logId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            logRepository.save(new TblFlameLog(logId, outbDtm, streamId, buildingCd));
        }

        // 2) event_hist : 건물당 1건. 이미 활성 건물이면 즉시 스킵.
        if (activeFlameBuildings.contains(buildingCd)) {
            return;
        }
        // 인메모리에 없으면 DB로 재확인 (재시작 대비 백스톱)
        if (!eventRepository.findByMappBuildingIdAndTypeAndClrDtm(buildingCd, "flame", "").isEmpty()) {
            activeFlameBuildings.add(buildingCd);
            return;
        }

        // 건물 첫 감지 → 생성 + 화면 전송
        List<TblCctv> cctvList = cctvRepository.findByBuildingLikeAndStreamIdNotLike("%" + buildingCd + "%", "%HIGH");
        // 8-1이 있고, 8도 있어서 한번 더 필터링 진행해준다.
        Set<String> cctvIds = cctvList.stream()
                .filter(cctv -> cctv.getBuilding().split(",")[0].contains(buildingCd))
                .map(TblCctv::getStreamId)
                .collect(Collectors.toCollection(HashSet::new));

        String seqn = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        TblEventHist newEventHist = new TblEventHist(seqn, "flame", outbDtm, "", buildingCd,
                Strings.join(cctvIds, ','), streamId);
        eventRepository.save(newEventHist);
        activeFlameBuildings.add(buildingCd);
        ws.sendToWeb(newEventHist);
        log.info("[불꽃 이벤트 발생 : {} 건물]", buildingCd);
    }

    // 매일 자정: 미해제 불꽃 이벤트 일괄 해제 + 활성 건물 초기화
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void clearFlameAtMidnight() {
        String clrDtm = LocalDateTime.now(KST).format(OUT);
        int cnt = eventRepository.clearAllUnresolvedFlame(clrDtm);
        activeFlameBuildings.clear();
        log.info("[자정 불꽃 이벤트 일괄 해제] {}건 해제, 활성 건물 초기화", cnt);
    }

    public static String formatEpochAuto(long ts) {
        // 10자리면 초, 13자리면 밀리초로 간주
        int len = String.valueOf(Math.abs(ts)).length();
        Instant instant = (len <= 10) ? Instant.ofEpochSecond(ts) : Instant.ofEpochMilli(ts);
        return instant.atZone(KST).format(OUT);
    }
}
