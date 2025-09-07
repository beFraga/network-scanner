package com.seguranca.rede.scanner.Services;

import com.seguranca.rede.scanner.PacketInfo.HttpInfos;
import com.seguranca.rede.scanner.PacketInfo.TcpInfos;
import com.seguranca.rede.scanner.Services.HTTP.HttpTrafficInterceptor;
import com.seguranca.rede.scanner.Services.TCP.TcpTrafficInterceptor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class PacketCaptureService {
    private ExecutorService TCPCapture = Executors.newSingleThreadExecutor();
    private ExecutorService HTTPCapture = Executors.newSingleThreadExecutor();
    private ExecutorService connectPackets = Executors.newSingleThreadExecutor();

    private BlockingQueue<TcpInfos> tcpQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<HttpInfos> httpQueue = new LinkedBlockingQueue<>();

    private List<TcpInfos> tcpList = new ArrayList<>();
    Map<String, HttpInfos> connections = new ConcurrentHashMap<>();
    public void startCaptureTCP(int maxPackets) {
        TCPCapture.submit(() -> {
            try {
                tcpList = new TcpTrafficInterceptor().Scannear(maxPackets);
                for (TcpInfos tcpInfos : tcpList) {
                    tcpQueue.put(tcpInfos);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void startCaptureHTTP() {
        HTTPCapture.submit(() -> {
            try {
                new HttpTrafficInterceptor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void startConnectPackets() {
        connectPackets.submit(() -> {
            try {
                while (true) {
                    TcpInfos tcp = tcpQueue.take();
                    String key = tcp.getLocalAddress() + ":"
                                + tcp.getLocalPort() + "-"
                                + tcp.getRemoteAddress() + ":"
                                + tcp.getRemotePort();
                    connections.computeIfAbsent(key, k->new HttpInfos()).addTcpPacket(tcp);
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        connectPackets.submit(() -> {
            try {
                while (true) {
                    HttpInfos http = httpQueue.take();
                    String key = http.getLocalAddress() + ":" +
                            http.getLocalPort() + "-" +
                            http.getRemoteAddress() + ":" +
                            http.getRemotePort();
                    connections.putIfAbsent(key, http);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


}
