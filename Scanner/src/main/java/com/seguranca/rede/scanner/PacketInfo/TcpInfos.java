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

    private int sequenceNumber;

    public TcpInfos(String packetString, TcpPacket packet){
        this.localAddress = getSrcAddress(packet);
        this.remoteAddress = getDestAddress(packet);
        this.localPort = Integer.parseInt(packet.getHeader().getSrcPort().name());
        this.remotePort = Integer.parseInt(packet.getHeader().getDstPort().name());
        this.sequenceNumber = packet.getHeader().getSequenceNumber();
    }

    private String getSrcAddress(Packet packet){
        if (packet.contains(IpV4Packet.class)){
            IpV4Packet ipv4 = packet.get(IpV4Packet.class);
            return ipv4.getHeader().getSrcAddr().getHostAddress();
        }
        if (packet.contains(IpV6Packet.class)){
            IpV6Packet ipv6 = packet.get(IpV6Packet.class);
            return ipv6.getHeader().getSrcAddr().getHostAddress();
        }
        if (packet.contains(IpPacket.class)){
            IpPacket ip = packet.get(IpPacket.class);
            return ip.getHeader().getSrcAddr().getHostAddress();
        }
        return null;
    }

    private String getDestAddress(Packet packet){
        if (packet.contains(IpV4Packet.class)){
            IpV4Packet ipv4 = packet.get(IpV4Packet.class);
            return ipv4.getHeader().getDstAddr().getHostAddress();
        }
        if (packet.contains(IpV6Packet.class)){
            IpV6Packet ipv6 = packet.get(IpV6Packet.class);
            return ipv6.getHeader().getDstAddr().getHostAddress();
        }
        if (packet.contains(IpPacket.class)){
            IpPacket ip = packet.get(IpPacket.class);
            return ip.getHeader().getDstAddr().getHostAddress();
        }
        return null;
    }

}
