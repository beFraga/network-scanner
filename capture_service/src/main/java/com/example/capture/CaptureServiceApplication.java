package com.example.capture;

import com.example.capture.External.gRPCClient;
import com.example.common.PacketInfo.HttpInfos;
import com.example.common.PacketInfo.TcpInfos;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

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