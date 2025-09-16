package com.seguranca.rede.scanner.PacketInfo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pcap4j.packet.*;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
public class TcpInfos {
    private String localAddress;
    private String remoteAddress;

    private int localPort;
    private int remotePort;

    private Long sequenceNumber;
    private Packet payload;

    public TcpInfos(IpPacket ipPacket, TcpPacket tcpPacket){
        this.localAddress = ipPacket.getHeader().getSrcAddr().getHostAddress();
        this.remoteAddress = ipPacket.getHeader().getDstAddr().getHostAddress();
        this.localPort = tcpPacket.getHeader().getSrcPort().valueAsInt();
        this.remotePort = tcpPacket.getHeader().getDstPort().valueAsInt();
        this.sequenceNumber = Long.valueOf(tcpPacket.getHeader().getSequenceNumber());
        this.payload = tcpPacket.getPayload();

    }

}
