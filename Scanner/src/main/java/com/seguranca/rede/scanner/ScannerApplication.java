package com.seguranca.rede.scanner;

import com.seguranca.rede.scanner.Services.PacketCaptureService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class ScannerApplication implements ApplicationRunner {

    private final PacketCaptureService packetCaptureService;

    // o Spring injeta automaticamente
    public ScannerApplication(PacketCaptureService packetCaptureService) {
        this.packetCaptureService = packetCaptureService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ScannerApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            int interval = 30;
            packetCaptureService.startConnectPackets();
            packetCaptureService.schedulePrintTask(interval);
        } catch (Exception e) {
            System.out.println("Erro no começo de captura de pacotes: " + e.getMessage());
        }
    }
}