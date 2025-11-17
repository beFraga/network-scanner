package com.seguranca.rede.scanner.ProjectPatterns.one;

import com.seguranca.rede.scanner.Model.PacketInfo.TcpInfos;

public class TcpLeaf implements NetworkComponent {

    private final String sourceIp;
    private final String destIp;
    private final int sourcePort;
    private final int destPort;
    private final int byteSize; // tamanho real em bytes

    public TcpLeaf(String sourceIp, String destIp, int sourcePort, int destPort, int byteSize) {
        this.sourceIp = sourceIp;
        this.destIp = destIp;
        this.sourcePort = sourcePort;
        this.destPort = destPort;
        this.byteSize = byteSize;
    }

    @Override
    public void showInfo() {
        System.out.println("TCP Packet:");
        System.out.println(" - Source: " + sourceIp + ":" + sourcePort);
        System.out.println(" - Destination: " + destIp + ":" + destPort);
        System.out.println("sum: " + byteSize);
        System.out.println();
    }

    @Override
    public int getByteSize() {
        return byteSize;
    }
}
