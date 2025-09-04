package com.seguranca.rede.scanner.PacketInfo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pcap4j.packet.Packet;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
public class TCPinfos {
    Packet.Header header;
    String localAdress;
    String remoteAdress;

    public TCPinfos(Packet packet){
        this.header = packet.getHeader();
    }
}
