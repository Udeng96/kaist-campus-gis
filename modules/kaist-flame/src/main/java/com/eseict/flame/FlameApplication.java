package com.eseict.flame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FlameApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlameApplication.class, args);
    }

}
