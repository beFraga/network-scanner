package com.seguranca.rede.scanner.Services;

import com.seguranca.rede.scanner.PacketInfo.HttpInfos;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class PacketAuxiliarFunctions {

    public String generateKey(String locaddr, int locport, String dstaddr, int dstport) {
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

    public void printConnections(Map<String, HttpInfos> connections, Set<String> printedKeys) {
        System.out.println("Novas conexões:");
        connections.forEach((key, value) -> {
            if (value.getMethod() == null) return;
            if (!printedKeys.contains(key)) {
                System.out.println("-------------------------------------------");
                System.out.println(key + ": ");
                System.out.println("URL: " + value.getUri());
                System.out.println("Método: " + value.getMethod());
                System.out.println("Protocolo: " + value.getProtocol());

                if (value.getTcpPackets() != null) {
                    value.getTcpPackets().forEach(t -> {
                        System.out.println("Porta local: " + t.getLocalPort());
                        System.out.println("Porta remota: " + t.getRemotePort());
                    });
                }
                printedKeys.add(key);
            }
        });
        System.out.println("-------------------------------------------");
    }
}
