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