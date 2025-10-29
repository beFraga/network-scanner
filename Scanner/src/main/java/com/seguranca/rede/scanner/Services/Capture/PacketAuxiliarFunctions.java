package com.seguranca.rede.scanner.Services.Capture;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.seguranca.rede.scanner.Model.PacketInfo.HttpInfos;
import com.seguranca.rede.scanner.Model.PacketInfo.TcpInfos;
import com.seguranca.rede.scanner.Model.User;
import com.seguranca.rede.scanner.Repository.HttpRepository;
import com.seguranca.rede.scanner.Repository.TcpRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
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
        Set<HttpInfos> novos = new HashSet<>();
        connections.forEach((key, tree) -> {
            tree.forEach((seqNumber, httpInfo) -> {
                if (!savedData.contains(httpInfo)) {
                    if (httpInfo.getMethod() != null) {
                        // associa o usuário e salva HTTP + TCPs
                        httpInfo.setUser(user);
                        httpInfo.getTcpPackets().forEach(t -> {
                            t.setFlag(false);
                            t.setHttpInfos(httpInfo);
                        });
                        httpRepository.save(httpInfo);
                        savedData.add(httpInfo);
                        novos.add(httpInfo);
                        System.out.println("✅ HTTP salvo: " + httpInfo.getUri());
                    }
                }
            });
        });

        if (!novos.isEmpty()) {
            createJson(novos, user);
        }
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

    @Transactional
    public void createJson(Set<HttpInfos> httpInfos, User user) {
        try {
            // cada TCP vira um item da lista
            List<Map<String, Object>> flatPackets = httpInfos.stream()
                    .flatMap(http -> http.getTcpPackets().stream()
                            .map(tcp -> Map.<String, Object>of(
                                    "method", http.getMethod(),
                                    "protocol", http.getProtocol(),
                                    "id", tcp.getId(),
                                    "sequenceNumber", tcp.getSequenceNumber(),
                                    "localAddress", tcp.getLocalAddress(),
                                    "remoteAddress", tcp.getRemoteAddress(),
                                    "remotePort", tcp.getRemotePort(),
                                    "payloadSize", (tcp.getPayload() != null) ? tcp.getPayload().length() : 0
                            ))
                    )
                    .toList();

            // Configura o ObjectMapper
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            // Cria diretório e arquivo
            Path outputDir = Paths.get("captures");
            Files.createDirectories(outputDir);
            String filename = "captures/capture_" + System.currentTimeMillis() + ".json";

            // Escreve o JSON diretamente como lista
            mapper.writeValue(Paths.get(filename).toFile(), flatPackets);

            System.out.println("📄 JSON achatado salvo em: " + filename);
        } catch (Exception e) {
            System.err.println("❌ Erro ao criar JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
