package com.seguranca.rede.scanner.Services;

import com.seguranca.rede.scanner.PacketInfo.HttpInfos;
import com.seguranca.rede.scanner.PacketInfo.TcpInfos;
import com.seguranca.rede.scanner.Services.TCP.TcpTrafficInterceptor;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

@Service
public class PacketCaptureService{
    // Threads
    private ExecutorService TCPCapture = Executors.newSingleThreadExecutor();
    private ExecutorService connectTCP = Executors.newSingleThreadExecutor();
    private ExecutorService connectHTTP = Executors.newSingleThreadExecutor();
    private ScheduledExecutorService printScheduler = Executors.newSingleThreadScheduledExecutor();

    // Data structures
    private BlockingQueue<TcpInfos> tcpQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<HttpInfos> httpQueue;
    public PacketCaptureService(BlockingQueue<HttpInfos> httpQueue) {
        this.httpQueue = httpQueue;
    }
    Map<String, HttpInfos> connections = new ConcurrentHashMap<>();
    private Set<String> printedKeys = ConcurrentHashMap.newKeySet();

    // Auxiliar Function
    PacketAuxiliarFunctions aux = new PacketAuxiliarFunctions();

    // Packet capturing and Map generating
    public void startConnectPackets() {
        TCPCapture.submit(() -> {
            try {
                new TcpTrafficInterceptor(tcpQueue).Scannear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        connectTCP.submit(() -> {
            try {
                while (true) {
                    TcpInfos tcp = tcpQueue.take();
                    String key = aux.generateKey(tcp.getLocalAddress(),  tcp.getLocalPort(),
                            tcp.getRemoteAddress(), tcp.getRemotePort());
                    // System.out.println("tcp-key" + key);
                    connections.computeIfAbsent(key, k->new HttpInfos()).addTcpPacket(tcp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        connectHTTP.submit(() -> {
            try {
                while (true) {
                    HttpInfos http = httpQueue.take();
                    String key = aux.generateKey(http.getLocalAddress(), http.getLocalPort(),
                            http.getRemoteAddress(), http.getRemotePort());
                    // System.out.println("http-key" + key);
                    connections.computeIfPresent(key, (k, existingHttpInfos)-> {
                        existingHttpInfos.setUri(http.getUri());
                        existingHttpInfos.setMethod(http.getMethod());
                        existingHttpInfos.setProtocol(http.getProtocol());
                        existingHttpInfos.setTcpPackets(http.getTcpPackets());
                        return existingHttpInfos;
                    });
                    connections.putIfAbsent(key, http);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    // Packets display
    public void schedulePrintTask(int seconds) {
        Runnable printTask = () -> {
            aux.printConnections(connections, printedKeys);
        };
        printScheduler.scheduleAtFixedRate(printTask, 5, seconds, TimeUnit.SECONDS);
    }
}
