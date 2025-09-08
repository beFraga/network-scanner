package com.seguranca.rede.scanner;

import com.seguranca.rede.scanner.PacketInfo.HttpInfos;
import com.seguranca.rede.scanner.Services.PacketCaptureService;
import org.pcap4j.core.PcapNativeException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class ScannerApplication {
    

    public static void main(String[] args) throws UnknownHostException, PcapNativeException {
        SpringApplication.run(ScannerApplication.class, args);
        BlockingQueue<HttpInfos> httpQueue = null;
        PacketCaptureService scanner = new PacketCaptureService(httpQueue);
        scanner.startCaptureTCP(5);
        scanner.startConnectPackets();
    }
}