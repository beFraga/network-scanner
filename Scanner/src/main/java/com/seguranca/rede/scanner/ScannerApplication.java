package com.seguranca.rede.scanner;

import com.seguranca.rede.scanner.ProjectPatterns.one.HttpComposite;
import com.seguranca.rede.scanner.ProjectPatterns.one.TcpLeaf;
import com.seguranca.rede.scanner.Services.Capture.PacketCaptureService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScannerApplication{

    public static void main(String[] args) {
        SpringApplication.run(ScannerApplication.class, args);
        HttpComposite http = new HttpComposite("GET", "/home", 90);

        TcpLeaf tcp1 = new TcpLeaf("192.168.0.2", "192.168.0.10", 5000, 80, 50);
        TcpLeaf tcp2 = new TcpLeaf("192.168.0.2", "192.168.0.10", 5001, 80, 60);

        http.addComponent(tcp1);
        http.addComponent(tcp2);

        http.showInfo();
    }

}