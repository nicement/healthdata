package com.healthdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class HealthdataApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthdataApplication.class, args);
    }

}
