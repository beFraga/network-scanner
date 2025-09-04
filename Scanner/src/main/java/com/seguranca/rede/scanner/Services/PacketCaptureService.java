package com.seguranca.rede.scanner.Services;

import com.seguranca.rede.scanner.PacketInfo.HttpInfos;
import com.seguranca.rede.scanner.PacketInfo.TCPinfos;
import com.seguranca.rede.scanner.Services.HTTP.HttpTrafficInterceptor;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class PacketCaptureService {
    private ExecutorService capturaTCP = Executors.newSingleThreadExecutor();
    private ExecutorService capturaHTTP = Executors.newSingleThreadExecutor();
    private ExecutorService conectarPacotes = Executors.newSingleThreadExecutor();

    private BlockingQueue<TCPinfos> tcpQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<HttpInfos> httpQueue = new LinkedBlockingQueue<>();

    public void startCaptureTCP(int maxPackets) {
        capturaTCP.submit(() -> {
            try {
                new com.seguranca.rede.scanner.Services.TCP.Scanner().Scannear(maxPackets);
                for (int i = 0; i < maxPackets; i++) {
                    TCPinfos tcp = new TCPinfos();
                    tcpQueue.put(tcp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void startCaptureHTTP() {
        capturaHTTP.submit(() -> {
            try {
                new HttpTrafficInterceptor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void startConectarPacotes() {
        conectarPacotes.submit(() -> {
            try {
                while (true) {
                    TCPinfos tcp = tcpQueue.take();
                    HttpInfos http = httpQueue.take();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


}
