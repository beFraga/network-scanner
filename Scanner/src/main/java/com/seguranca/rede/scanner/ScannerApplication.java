package com.seguranca.rede.scanner;

import com.seguranca.rede.scanner.PacketInfo.HttpInfos;
import com.seguranca.rede.scanner.Services.PacketCaptureService;
import org.pcap4j.core.PcapNativeException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;

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
        // roda logo após o contexto subir
        packetCaptureService.startConnectPackets(100);

    }
}