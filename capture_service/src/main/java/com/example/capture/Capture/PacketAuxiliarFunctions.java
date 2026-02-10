package com.example.capture.Capture;

import com.example.capture.External.gRPCClient;
import com.example.common.UserInfo.User;
import com.example.common.PacketInfo.*;
import com.example.capture.Repository.HttpRepository;
import com.example.capture.Repository.TcpRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PacketAuxiliarFunctions {

    private final HttpRepository httpRepository;
    private final TcpRepository tcpRepository;

    public PacketAuxiliarFunctions(HttpRepository httpRepository, TcpRepository tcpRepository) {
        this.httpRepository = httpRepository;
        this.tcpRepository = tcpRepository;
    }

    public String generateKey(String locaddr, int locport, String dstaddr, int dstport) {
        if (locaddr == null || dstaddr == null) {
            return null;
        }

        if (locaddr.compareTo(dstaddr) > 0) {
            return dstaddr + ":" + dstport + ":" + locaddr + ":" + locport;
        } else if (locaddr.compareTo(dstaddr) < 0) {
            return locaddr + ":" + locport + ":" + dstaddr + ":" + dstport;
        } else {
            if (locport < dstport) {
                return locaddr + ":" + locport + ":" + dstaddr + ":" + dstport;
            } else {
                return dstaddr + ":" + dstport + ":" + locaddr + ":" + locport;
            }
        }
    }

    public void printConnections(Map<String, TreeMap<Long, HttpInfos>> connections, Set<HttpInfos> printedHttp) {
        System.out.println("Printing connections:");
        connections.forEach((key, tree) -> tree.forEach((seqNumber, httpInfo) -> {
            if (!printedHttp.contains(httpInfo)) {
                if (httpInfo.getMethod() != null) {
                    System.out.println("-------------------------------------------");
                    System.out.println(key + ": ");
                    System.out.println("URI: " + httpInfo.getUri());
                    System.out.println("Method: " + httpInfo.getMethod());
                    System.out.println("Protocol: " + httpInfo.getProtocol());
                    System.out.println("Local port (HTTP): " + httpInfo.getLocalPort());
                    System.out.println("Remote port (HTTP): " + httpInfo.getRemotePort());
                    System.out.println("Local address: " + httpInfo.getLocalAddress());
                    System.out.println("Remote address: " +  httpInfo.getRemoteAddress());
                    if (httpInfo.getTcpPackets() != null) {
                        httpInfo.getTcpPackets().forEach(t -> {
                            System.out.println("Local port: " + t.getLocalPort());
                            System.out.println("Remote port: " + t.getRemotePort());
                        });
                    }
                    printedHttp.add(httpInfo);
                }
            }
        }));
    }

    public boolean isNewHttpRequest(TcpInfos tcp) {
        if (tcp.getPayloadRaw() == null) {
            return false;
        }
        byte[] payloadData = tcp.getPayloadRaw();
        if (payloadData.length < 4) {
            return false;
        }
        String payloadHeader = new String(payloadData, 0, Math.min(payloadData.length, 10));
        return payloadHeader.startsWith("GET ") ||
                payloadHeader.startsWith("POST ") ||
                payloadHeader.startsWith("PUT ") ||
                payloadHeader.startsWith("DELETE ") ||
                payloadHeader.startsWith("HEAD ") ||
                payloadHeader.startsWith("OPTIONS ") ||
                payloadHeader.startsWith("TRACE ") ||
                payloadHeader.startsWith("CONNECT ");
    }

    @Transactional
    public void saveData(Map<String, TreeMap<Long, HttpInfos>> connections, Set<HttpInfos> savedData, User user) {
        System.out.println("💾 Salvando dados:");
        List<HttpInfos> novos = new ArrayList<>();
        connections.forEach((key, tree) -> tree.forEach((seqNumber, httpInfo) -> {
            if (!savedData.contains(httpInfo)) {
                if (httpInfo.getMethod() != null) {
                    // associates user and saves HTTP + TCPs
                    httpInfo.setUser(user);
                    httpInfo.getTcpPackets().forEach(t -> {
                        t.setFlag(false);
                        t.setHttpInfos(httpInfo);
                    });
                    httpRepository.save(httpInfo);
                    savedData.add(httpInfo);
                    novos.add(httpInfo);
                    System.out.println("✅ HTTP saved: " + httpInfo.getUri());
                }
            }
        }));
        try {
            sendToModel(novos);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendToModel(List<HttpInfos> httpInfos) throws InterruptedException {
        System.out.println("Starting gRPC...");

        String grpcHost = System.getenv().getOrDefault("GRPC_HOST", "aicpp");
        int grpcPort = Integer.parseInt(
                System.getenv().getOrDefault("GRPC_PORT", "50051")
        );
        gRPCClient client = new gRPCClient(tcpRepository, grpcHost, grpcPort);

        client.sendWindow(httpInfos);

        // Search for results
        var qtd = client.getResults();

        System.out.println("📦 Results received: " + qtd);
        client.shutdown();
    }

    // Flags updates occurs in grpcClient class
}

