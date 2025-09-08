package com.seguranca.rede.scanner.Services;

import com.seguranca.rede.scanner.PacketInfo.HttpInfos;
import com.seguranca.rede.scanner.PacketInfo.TcpInfos;
import com.seguranca.rede.scanner.Services.HTTP.HttpTrafficInterceptor;
import com.seguranca.rede.scanner.Services.TCP.TcpTrafficInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class PacketCaptureService {
    private ExecutorService TCPCapture = Executors.newSingleThreadExecutor();
    private ExecutorService connectTCP = Executors.newSingleThreadExecutor();
    private ExecutorService connectHTTP = Executors.newSingleThreadExecutor();

    private BlockingQueue<TcpInfos> tcpQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<HttpInfos> httpQueue;

    public PacketCaptureService(BlockingQueue<HttpInfos> httpQueue) {
        this.httpQueue = httpQueue;
    }

    private List<TcpInfos> tcpList = new ArrayList<>();
    Map<String, HttpInfos> connections = new ConcurrentHashMap<>();

    public void startCaptureTCP(int seconds) {
        TCPCapture.submit(() -> {
            try {
                tcpList = new TcpTrafficInterceptor().Scannear(seconds);
                for (TcpInfos tcpInfos : tcpList) {
                    tcpQueue.put(tcpInfos);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void startConnectPackets() {
        connectTCP.submit(() -> {
            try {
                while (true) {
                    System.out.println("777");
                    TcpInfos tcp = tcpQueue.take();
                    String key = tcp.getLocalAddress() + ":"
                                + tcp.getLocalPort() + "-"
                                + tcp.getRemoteAddress() + ":"
                                + tcp.getRemotePort();
                    System.out.println("tcp-key" + key);
                    connections.computeIfAbsent(key, k->new HttpInfos()).addTcpPacket(tcp);
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        connectHTTP.submit(() -> {
            try {
                while (true) {
                    System.out.println("7779");
                    HttpInfos http = httpQueue.take();
                    String key = http.getLocalAddress() + ":" +
                            http.getLocalPort() + "-" +
                            http.getRemoteAddress() + ":" +
                            http.getRemotePort();
                    System.out.println("http-key" + key);
                    connections.putIfAbsent(key, http);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


}
