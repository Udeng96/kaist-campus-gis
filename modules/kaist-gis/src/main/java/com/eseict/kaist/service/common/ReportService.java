package com.eseict.kaist.service.common;

import com.eseict.kaist.repository.WeatherRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

    private final WeatherRepository weatherRepository;

    @Value("${api.report.url}")
    private String reportUrl;

    @Value("${api.report.key}")
    private String reportKey;

    @Value("${api.report.areaId}")
    private String areaId;

    public void updateReport(){
        log.info("report update start");

        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String reportContent = getReportInfo();

        if(reportContent != null){
            int updated = weatherRepository.updateReportContent(reportContent, today);
            if(updated > 0){
                log.info("report content updated : {}", reportContent);
            }else{
                log.info("today's weather row not found yet; report will be applied next cycle");
            }
        }else{
            log.info("report update failed");
        }
    }


    public String getReportInfo(){
        String url = reportUrl + "?serviceKey=" + reportKey + "&dataType=JSON" + "&numOfRows=100" + "&pageNo=1" + "&stnId=" + areaId;

        log.info("url::{}", url);
        JsonObject reportJson = request(url);

        if(reportJson == null){
            return null;
        }

        String resultCode =reportJson.getAsJsonObject("response").getAsJsonObject("header").get("resultCode").getAsString();

        if(resultCode.equals("03")){
            return "특보 없음";
        }else if(!resultCode.equals("00")){
            log.info("report API error : resultCode = {}", resultCode);
            return null;
        }

        JsonObject body = reportJson.getAsJsonObject("response").getAsJsonObject("body");
        if (!body.has("items") || !body.get("items").isJsonObject()) return "특보 없음";

        JsonObject itemsObj = body.getAsJsonObject("items");
        if (!itemsObj.has("item")) return "특보 없음";

        // 통보문이 여러 건이면 item이 배열, 1건이면 객체로 내려온다
        JsonArray items = new JsonArray();
        JsonElement itemEl = itemsObj.get("item");
        if (itemEl.isJsonArray()) {
            items = itemEl.getAsJsonArray();
        } else if (itemEl.isJsonObject()) {
            items.add(itemEl);
        }

        // getWthrWrnMsg 통보문은 매번 현재 발효 상태 전체를 담으므로, 가장 최근(tmFc 최대) 한 건이면 충분하다.
        JsonObject latest = null;
        long maxTmFc = -1;
        for (JsonElement e : items) {
            JsonObject o = e.getAsJsonObject();
            long tmFc = o.has("tmFc") ? o.get("tmFc").getAsLong() : 0L;
            if (tmFc >= maxTmFc) { maxTmFc = tmFc; latest = o; }
        }
        if (latest == null || !latest.has("t6")) return "특보 없음";

        // t6 본문에서 '대전'이 포함된 줄의 특보 종류만 추출
        // 예: "o 폭염경보 : 충청남도(...), 대전, 세종(...)" -> "폭염경보"
        LinkedHashSet<String> types = new LinkedHashSet<>();
        for (String line : latest.get("t6").getAsString().split("\\r?\\n")) {
            String s = line.trim();
            if (s.isEmpty() || !s.contains("대전")) continue;
            s = s.replaceFirst("^o\\s*", "");
            int colon = s.indexOf(':');
            String type = (colon >= 0 ? s.substring(0, colon) : s).trim();
            if (!type.isEmpty()) types.add(type);
        }
        if (types.isEmpty()) return "특보 없음";

        String result = String.join(", ", types);

        // 발효시각(t5, yyyyMMddHHmm) -> " (MM-DD HH:mm 발효)"
        if (latest.has("t5")) {
            String t5 = latest.get("t5").getAsString().trim();
            if (t5.length() == 12) {
                result += " (" + t5.substring(4, 6) + "-" + t5.substring(6, 8) + " "
                        + t5.substring(8, 10) + ":" + t5.substring(10, 12) + " 발효)";
            }
        }
        return result;
    }

    private JsonObject request(String urlStr) {
        URL url = null;
        try {
            url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            return JsonParser.parseReader(br).getAsJsonObject();
        } catch (IOException e) {
            log.error("request error :{}", e.getMessage());
            return null;
        }
    }

}
