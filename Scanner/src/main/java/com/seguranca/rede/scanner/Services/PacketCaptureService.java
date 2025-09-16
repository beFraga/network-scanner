package com.seguranca.rede.scanner.Services;

import com.seguranca.rede.scanner.PacketInfo.HttpInfos;
import com.seguranca.rede.scanner.PacketInfo.TcpInfos;
import com.seguranca.rede.scanner.Services.TCP.TcpTrafficInterceptor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
    Map<String, TreeMap<Long, HttpInfos>> connections_repeat = new ConcurrentHashMap<>();
    private Set<String> printedKeys = ConcurrentHashMap.newKeySet();

    // Auxiliar Function
    PacketAuxiliarFunctions aux = new PacketAuxiliarFunctions();

    // Packet capturing and Map generating
    public void startConnectPackets() {
        TCPCapture.submit(() -> {
            try {
                new TcpTrafficInterceptor(tcpQueue).Scan();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        connectTCP.submit(() -> {
            try {
                while (true) {
                    TcpInfos tcp = tcpQueue.take();
                    String key = aux.generateKey(tcp.getLocalAddress(), tcp.getLocalPort(),
                            tcp.getRemoteAddress(), tcp.getRemotePort());

                    if (key != null) {
                        boolean isNewHttpRequest = aux.isNewHttpRequest(tcp);
                        if (isNewHttpRequest) {
                            HttpInfos newHttpInfo = new HttpInfos();
                            if (tcp.getPayload() != null) {
                                newHttpInfo.setHeaderPayload(tcp.getPayload().getRawData());
                            }
                            connections_repeat.computeIfAbsent(key, k -> new TreeMap<>()).put(tcp.getSequenceNumber(), new HttpInfos());
                        }

                        TreeMap<Long, HttpInfos> httpTree = connections_repeat.get(key);
                        if (httpTree != null) {
                            Long startSeqNumber = httpTree.floorKey(tcp.getSequenceNumber());
                            if (startSeqNumber != null) {
                                HttpInfos httpInfo = httpTree.get(startSeqNumber);
                                httpInfo.addTcpPacket(tcp);
                            }
                        }
                    }
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        });

        connectHTTP.submit(() -> {
            try {
                long timeoutMilis = 3500;
                while (true) {
                    HttpInfos http = httpQueue.take();
                    String key = aux.generateKey(http.getLocalAddress(), http.getLocalPort(),
                            http.getRemoteAddress(), http.getRemotePort());
                    long startTime = System.currentTimeMillis();
                    while (System.currentTimeMillis() - startTime < timeoutMilis) {
                        if (connections_repeat.containsKey(key)) {
                            break;
                        }
                        Thread.sleep(100);
                    }
                    if (connections_repeat.containsKey(key)) {
                        TreeMap<Long, HttpInfos> httpTree = connections_repeat.get(key);
                        httpTree.values().stream()
                                .filter(httpInfos -> httpInfos.getUri() == null)
                                .filter(httpInfo -> Arrays.equals(httpInfo.getHeaderPayload(), http.getHeaderPayload()))
                                .findFirst()
                                .ifPresent(existingHttpInfos -> {
                                    existingHttpInfos.setUri(http.getUri());
                                    existingHttpInfos.setMethod(http.getMethod());
                                    existingHttpInfos.setProtocol(http.getProtocol());
                                });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    // Packets display
    public void schedulePrintTask(int seconds) {
        Runnable printTask = () -> {
            aux.printConnections(connections_repeat, printedKeys);
        };
        printScheduler.scheduleAtFixedRate(printTask, 5, seconds, TimeUnit.SECONDS);
    }
}
