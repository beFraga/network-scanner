package com.seguranca.rede.scanner;

import com.seguranca.rede.scanner.Services.TCP.PacketCaptureService;
import org.pcap4j.core.PcapNativeException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import java.net.UnknownHostException;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class ScannerApplication {

    public static void main(String[] args) throws UnknownHostException, PcapNativeException {

        SpringApplication.run(ScannerApplication.class, args);
        PacketCaptureService scanner = new PacketCaptureService();
        scanner.startCapture(500);
    }
}