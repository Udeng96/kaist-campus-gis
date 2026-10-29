package com.eseict.kaist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KaistApplication {

    public static void main(String[] args) {
        SpringApplication.run(KaistApplication.class, args);
    }

}
