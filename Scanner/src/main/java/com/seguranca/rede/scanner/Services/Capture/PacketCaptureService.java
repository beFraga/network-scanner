package com.seguranca.rede.scanner.Services.Capture;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seguranca.rede.scanner.Model.PacketInfo.HttpInfos;
import com.seguranca.rede.scanner.Model.PacketInfo.TcpInfos;
import com.seguranca.rede.scanner.Model.User;
import com.seguranca.rede.scanner.Repository.HttpRepository;
import com.seguranca.rede.scanner.Repository.TcpRepository;
import com.seguranca.rede.scanner.Repository.UserRepository;
import com.seguranca.rede.scanner.Services.TCP.TcpTrafficInterceptor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

@Service
@Setter
public class PacketCaptureService{
    // Threads
    private ExecutorService TCPCapture = Executors.newSingleThreadExecutor();
    private ExecutorService connectTCP = Executors.newSingleThreadExecutor();
    private ExecutorService connectHTTP = Executors.newSingleThreadExecutor();
    private ScheduledExecutorService printScheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledExecutorService BDScheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledExecutorService flagReader = Executors.newSingleThreadScheduledExecutor();

    // Data structures
    private BlockingQueue<TcpInfos> tcpQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<HttpInfos> httpQueue;
    public PacketCaptureService(BlockingQueue<HttpInfos> httpQueue, HttpRepository httpRepository, TcpRepository tcpRepository, PacketAuxiliarFunctions aux) {
        this.httpQueue = httpQueue;
        this.httpRepository = httpRepository;
        this.tcpRepository = tcpRepository;
        this.aux = aux;
    }
    Map<String, TreeMap<Long, HttpInfos>> connections_repeat = new ConcurrentHashMap<>();
    private Set<HttpInfos> printedHttp = ConcurrentHashMap.newKeySet();
    private Set<HttpInfos> savedHttp = ConcurrentHashMap.newKeySet();
    private final HttpRepository httpRepository;
    private final TcpRepository tcpRepository;

    // Auxiliar Function
    private final PacketAuxiliarFunctions aux;



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
    public void schedulePrintTask(int seconds, User user) {
        Runnable printTask = () -> {
            aux.printConnections(connections_repeat, printedHttp);
        };
        Runnable saveDataTask = () -> {
            aux.saveData(connections_repeat, savedHttp, user);
        };
        Runnable getFlags = () -> {
            aux.getJson("../../");
        };
        printScheduler.scheduleAtFixedRate(printTask, 5, seconds, TimeUnit.SECONDS);
        BDScheduler.scheduleAtFixedRate(saveDataTask, 6, seconds, TimeUnit.SECONDS);
        flagReader.scheduleAtFixedRate(getFlags, 7,  seconds, TimeUnit.SECONDS);
    }
}
