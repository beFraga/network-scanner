package com.example.capture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
// 1. Make spring find the common package
@EntityScan(basePackages = {
        "com.example.common"
})
// 2. Make spring find repositories on capture_service
@EnableJpaRepositories(basePackages = {
        "com.example.capture.Repository"
})
public class CaptureServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaptureServiceApplication.class, args);
    }

}