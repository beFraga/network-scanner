package com.seguranca.rede.scanner;

import com.seguranca.rede.scanner.Controller.TestController;
import com.seguranca.rede.scanner.Services.Scanner;
import org.aspectj.weaver.ast.Test;
import org.pcap4j.core.NotOpenException;
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
        /*Scanner scanner = new Scanner();
        try {
            scanner.Scannear();
        } catch (PcapNativeException | NotOpenException e) {
            throw new RuntimeException(e);
        }
        */
        TestController teste = new TestController();
        System.out.println(teste.ping());
    }

}
