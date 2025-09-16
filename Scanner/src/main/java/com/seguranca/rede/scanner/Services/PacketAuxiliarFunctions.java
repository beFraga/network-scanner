package com.seguranca.rede.scanner.Services;

import com.seguranca.rede.scanner.PacketInfo.HttpInfos;
import com.seguranca.rede.scanner.PacketInfo.TcpInfos;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Component
public class PacketAuxiliarFunctions {

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

    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printConnections(Map<String, TreeMap<Long, HttpInfos>> connections, Set<String> printedKeys) {
        clearConsole();
        System.out.println("Printing connections:");
        connections.forEach((key, tree) -> {
            if (!printedKeys.contains(key)) {
                tree.forEach((seqNumber, httpInfo) -> {
                    if (httpInfo.getMethod() != null) {
                        System.out.println("-------------------------------------------");
                        System.out.println(key + ": ");
                        System.out.println("URL: " + httpInfo.getUri());
                        System.out.println("Método: " + httpInfo.getMethod());
                        System.out.println("Protocolo: " + httpInfo.getProtocol());

                        if (httpInfo.getTcpPackets() != null) {
                            httpInfo.getTcpPackets().forEach(t -> {
                                System.out.println("Porta local: " + t.getLocalPort());
                                System.out.println("Porta remota: " + t.getRemotePort());
                            });
                        }
                    }
                });
                //printedKeys.add(key);
            }
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
