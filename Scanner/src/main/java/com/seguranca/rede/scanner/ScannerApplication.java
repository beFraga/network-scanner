package com.seguranca.rede.scanner;

import com.seguranca.rede.scanner.Services.Capture.PacketCaptureService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScannerApplication{

    public static void main(String[] args) {
        SpringApplication.run(ScannerApplication.class, args);
    }

}