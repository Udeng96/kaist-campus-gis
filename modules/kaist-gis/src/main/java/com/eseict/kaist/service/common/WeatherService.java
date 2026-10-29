package com.eseict.kaist.service.common;

import com.eseict.kaist.data.vo.publi.TblWeather;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

    private final WeatherRepository weatherRepository;

    @Value("${api.weather.url}")
    private String url;

    @Value("${api.weather.nx}")
    private String nx;

    @Value("${api.weather.ny}")
    private String ny;

    @Value("${api.weather.lat}")
    private String lat;

    @Value("${api.weather.lon}")
    private String lon;

    @Value("${api.weather.key}")
    private String key;

    @Value("${api.dust.station.url}")
    private String stationUrl;

    @Value("${api.dust.station.key}")
    private String stationKey;

    @Value("${api.dust.url}")
    private String dustUrl;

    @Value("${api.dust.key}")
    private String dustKey;


    // 초단기예보(getUltraSrtFcst)는 매시 30분 발표, 약 45분 후 제공.
    // 최신 가용 발표시각(HH30)을 반환한다.
    private String getBaseTimeForUltra() {
        LocalTime now = LocalTime.now();

        int h = now.getHour();
        int m = now.getMinute();

        if (m < 45) {
            h = (h == 0) ? 23 : h - 1;
        }
        return String.format("%02d30", h);
    }


    public TblWeather getWeather() {
        return weatherRepository.findFirstByOrderByRegDtmDesc();
    }

    public void updateWeather() {

        String baseDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = getBaseTimeForUltra(); //일반적으로 가장 최근 발표시각
        List<TblWeather> todayWeatherInfo = weatherRepository.findByRegDtm(baseDate);

        TblWeather weatherInfo = getWeatherInfo(baseDate, baseTime);
        if (weatherInfo == null) {
            log.info("weather update failed");
            return;
        }

        // 대기질은 실패해도 날씨 저장을 막지 않는다 (실패 시 미세먼지 등급만 '-')
        TblWeather result = getDustInfo(weatherInfo);
        if (result == null) {
            log.info("dust update failed; saving weather only");
            weatherInfo.setDust("-");
            weatherInfo.setUltraDust("-");
            result = weatherInfo;
        }

        result.setRegDtm(baseDate);
        result.setUpdDtm(baseDate + baseTime);
        log.info("weatherInfo:{} ", result);
        if (todayWeatherInfo.isEmpty()) {
            weatherRepository.save(result);
            log.info("weather saved");
        } else {
            weatherRepository.updateUpdDtm(baseDate + baseTime, baseDate);
        }

    }

    private TblWeather getWeatherInfo(String baseDate, String baseTime) {
        //날씨 및 하늘 상태 조회
        log.info("weather update start");
        String weatherUrl = url + "?serviceKey=" + key + "&numOfRows=100&pageNo=1&dataType=JSON" + "&base_date=" + baseDate + "&base_time=" + baseTime + "&nx=" + nx + "&ny=" + ny;

        JsonObject weatherJson = request(weatherUrl);

        if (weatherJson == null) {
            return null;
        }

        try {
            String resultCode = weatherJson.getAsJsonObject("response")
                    .getAsJsonObject("header")
                    .get("resultCode").getAsString();

            if (!resultCode.equals("00")) {
                log.info("weather API error : resultCode = {}", resultCode);
                return null;
            }

            JsonObject body = weatherJson.getAsJsonObject("response").getAsJsonObject("body");
            if (body == null || !body.has("items") || !body.get("items").isJsonObject()) return null;
            JsonObject itemsObj = body.getAsJsonObject("items");
            if (!itemsObj.has("item") || !itemsObj.get("item").isJsonArray()) return null;
            JsonArray weatherArray = itemsObj.getAsJsonArray("item");

            // 초단기예보는 여러 예보시각(fcstTime)을 담는다. 가장 이른(현재 근접) 시각만 사용.
            String minKey = null;
            for (JsonElement e : weatherArray) {
                JsonObject o = e.getAsJsonObject();
                String fKey = o.get("fcstDate").getAsString() + o.get("fcstTime").getAsString();
                if (minKey == null || fKey.compareTo(minKey) < 0) minKey = fKey;
            }

            String tmp = "";
            String sky = "";
            String pty = "";

            for (JsonElement e : weatherArray) {
                JsonObject o = e.getAsJsonObject();
                String fKey = o.get("fcstDate").getAsString() + o.get("fcstTime").getAsString();
                if (!fKey.equals(minKey)) continue;
                String category = o.get("category").getAsString();
                String value = o.get("fcstValue").getAsString();

                switch (category) {
                    case "T1H":
                        tmp = value;
                        break;
                    case "SKY":
                        sky = value;
                        break;
                    case "PTY":
                        pty = value;
                        break;
                }
            }

            String skyNm = sky.equals("1") ? "맑음" : sky.equals("3") ? "구름 많음" : "흐림";
            String ptyNm = pty.equals("0") ? "없음" : pty.equals("1") ? "비" : pty.equals("2") ? "비/눈" : pty.equals("3") ? "눈" : pty.equals("4") ? "소나기" : "기타";

            String skyCd = getSky(sky);
            String ptyCd = getPty(pty);

            return new TblWeather(tmp, skyNm, ptyNm, skyCd, ptyCd);
        } catch (Exception ex) {
            log.error("weather parse error : {}", ex.getMessage());
            return null;
        }
    }

    private TblWeather getDustInfo(TblWeather weatherInfo) {
        log.info("dust update start");

        String station = null;
        try {
            station = URLEncoder.encode("신성동", StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            log.info("failed to encode station name");
            return null;
        }

        String airUrl = dustUrl + "?serviceKey=" + dustKey + "&returnType=json&numOfRows=1&pageNo=1" + "&stationName=" + station + "&dataTerm=DAILY&ver=1.3";
        JsonObject airJson = request(airUrl);

        if (airJson == null) {
            return null;
        }

        try {
            String resultCode = airJson.getAsJsonObject("response")
                    .getAsJsonObject("header")
                    .get("resultCode").getAsString();

            if (!resultCode.equals("00")) {
                log.info("dust API error : resultCode = {}", resultCode);
                return null;
            }

            JsonObject body = airJson.getAsJsonObject("response").getAsJsonObject("body");
            if (body == null || !body.has("items") || !body.get("items").isJsonArray() || body.getAsJsonArray("items").isEmpty()) {
                weatherInfo.setDust(getPm10Grade(null));
                weatherInfo.setUltraDust(getPm25Grade(null));
                return weatherInfo;
            }

            JsonArray airArray = body.getAsJsonArray("items");

            String pm10 = "";
            String pm25 = "";

            for (JsonElement e : airArray) {
                JsonObject o = e.getAsJsonObject();

                pm10 = o.get("pm10Value").getAsString();
                pm25 = o.get("pm25Value").getAsString();
            }

            weatherInfo.setDust(getPm10Grade(pm10));
            weatherInfo.setUltraDust(getPm25Grade(pm25));

            return weatherInfo;
        } catch (Exception ex) {
            log.error("dust parse error : {}", ex.getMessage());
            return null;
        }
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

    public String getSky(String sky) {
        return sky.equals("1") ? "sun"
                : sky.equals("3") ? "sun-cloud"
                : "cloud";
    }

    public String getPty(String pty) {
        if (pty == null) return "기타";

        switch (pty) {
            case "0":
                return "sun";
            case "1":
                return "cloud-rain";
            case "2":
                return "rain-snow";
            case "3":
                return "cloud-snow";
            case "4":
                return "shower-rain";
            case "5":
                return "rain-less";
            case "6":
                return "snow-rain";
            case "7":
                return "snow-less";
            default:
                return "none";
        }
    }

    public String getPm10Grade(String pm10) {
        if (pm10 == null || pm10.equals("-") || pm10.trim().isEmpty()) return "-";

        int value = Integer.parseInt(pm10);
        if (value <= 30) return "좋음";
        else if (value <= 80) return "보통";
        else if (value <= 150) return "나쁨";
        else return "매우나쁨";
    }

    public String getPm25Grade(String pm25) {
        if (pm25 == null || pm25.equals("-") || pm25.trim().isEmpty()) return "-";

        int value = Integer.parseInt(pm25);
        if (value <= 15) return "좋음";
        else if (value <= 35) return "보통";
        else if (value <= 75) return "나쁨";
        else return "매우나쁨";
    }
}


