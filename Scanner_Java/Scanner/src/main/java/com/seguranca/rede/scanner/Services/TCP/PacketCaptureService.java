package com.seguranca.rede.scanner.Services.TCP;

import com.seguranca.rede.scanner.Services.TCP.Scanner;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class PacketCaptureService {
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public void startCapture(int maxPackets) {
        executor.submit(() -> {
            try {
                new com.seguranca.rede.scanner.Services.TCP.Scanner().Scannear(maxPackets);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
