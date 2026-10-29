package com.eseict.kaist.scheduler;


import com.eseict.kaist.service.common.ReportService;
import com.eseict.kaist.service.common.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeatherScheduler {

    private final WeatherService weatherService;
    private final ReportService reportService;

    @Scheduled(fixedDelay = 600000)
    public void updateWeather() {
        weatherService.updateWeather();
    }

    @Scheduled(fixedDelay = 300000)
    public void updateReport(){
        reportService.updateReport();
    }

}
