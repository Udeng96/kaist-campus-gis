package com.eseict.kaist.controller.common.openApi;

import com.eseict.kaist.data.vo.publi.TblWeather;
import com.eseict.kaist.service.common.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.eseict.kaist.config.ApiConstant.API_PRODUCES;

@RestController
@RequestMapping(value = "/weather", produces = API_PRODUCES)
@Slf4j
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/info")
    public ResponseEntity<TblWeather> getWeatherInfo() {
        return ResponseEntity.ok(weatherService.getWeather());
    }
}
