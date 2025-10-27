package com.seguranca.rede.scanner.Services.Capture;

import com.seguranca.rede.scanner.Model.PacketInfo.HttpInfos;
import com.seguranca.rede.scanner.Model.PacketInfo.TcpInfos;
import com.seguranca.rede.scanner.Model.User;
import com.seguranca.rede.scanner.Repository.HttpRepository;
import com.seguranca.rede.scanner.Repository.TcpRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
        connections.forEach((key, tree) -> {
            tree.forEach((seqNumber, httpInfo) -> {
                if (!printedHttp.contains(httpInfo)) {
                    if (httpInfo.getMethod() != null) {
                        System.out.println("-------------------------------------------");
                        System.out.println(key + ": ");
                        System.out.println("URI: " + httpInfo.getUri());
                        System.out.println("Método: " + httpInfo.getMethod());
                        System.out.println("Protocolo: " + httpInfo.getProtocol());
                        System.out.println("Porta local HTTP: " + httpInfo.getLocalPort());
                        System.out.println("Porta remota HTTP: " + httpInfo.getRemotePort());
                        System.out.println("Endereço local HTTP: " + httpInfo.getLocalAddress());
                        System.out.println("Endereço remoto HTTP " +  httpInfo.getRemoteAddress());
                        if (httpInfo.getTcpPackets() != null) {
                            httpInfo.getTcpPackets().forEach(t -> {
                                System.out.println("Porta local: " + t.getLocalPort());
                                System.out.println("Porta remota: " + t.getRemotePort());
                            });
                        }
                        printedHttp.add(httpInfo);
                    }


                }
            });
        });
    }

    @Transactional
    public void saveData(Map<String, TreeMap<Long, HttpInfos>> connections, Set<HttpInfos> savedData, User user) {
        System.out.println("💾 Salvando dados:");
        connections.forEach((key, tree) -> {
            tree.forEach((seqNumber, httpInfo) -> {
                if (!savedData.contains(httpInfo)) {
                    if (httpInfo.getMethod() != null) {
                        // associa o usuário e salva HTTP + TCPs
                        httpInfo.setUser(user);
                        httpInfo.getTcpPackets().forEach(t -> t.setHttpInfos(httpInfo));
                        httpRepository.save(httpInfo);
                        savedData.add(httpInfo);
                        System.out.println("✅ HTTP salvo: " + httpInfo.getUri());
                    }
                }
            });
        });
    }

    public boolean isNewHttpRequest(TcpInfos tcp) {
        if (tcp.getPayload() == null) {
            return false;
        }

        byte[] payloadData = tcp.getPayload().getRawData();

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
}
