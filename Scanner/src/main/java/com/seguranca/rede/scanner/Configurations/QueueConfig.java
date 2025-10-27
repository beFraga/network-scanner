package com.seguranca.rede.scanner.Configurations;

import com.seguranca.rede.scanner.Model.PacketInfo.HttpInfos;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class QueueConfig {
    @Bean
    public BlockingQueue<HttpInfos> httpQueue() {
            return new LinkedBlockingQueue<>();
    }
}
