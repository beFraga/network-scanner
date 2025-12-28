package com.example.capture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
// 1. Faz o Spring encontrar a classe User que está no módulo Common
@EntityScan(basePackages = {
        "com.example.common"
})
// 2. Faz o Spring encontrar o UserRepository que está no módulo Auth
@EnableJpaRepositories(basePackages = {
        "com.example.capture.Repository"
})
public class CaptureServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaptureServiceApplication.class, args);
    }
}