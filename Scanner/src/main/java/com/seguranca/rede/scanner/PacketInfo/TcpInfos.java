package com.seguranca.rede.scanner.PacketInfo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pcap4j.packet.Packet;
import org.springframework.stereotype.Component;

import static ch.qos.logback.classic.spi.CallerData.extract;


@Component
@Getter
@Setter
@NoArgsConstructor
public class TcpInfos {
    private String localAddress;
    private String remoteAddress;

    private int localPort;
    private int remotePort;

    public TcpInfos(String packet){
        this.localAddress = extract(packet, "Source address: ([^\\s]+)");
        this.remoteAddress = extract(packet, "Destination address: ([^\\s]+)");
        this.localPort = Integer.parseInt(extract(packet, "Source port: (\\d+)"));
        this.remotePort = Integer.parseInt(extract(packet, "Destination port: (\\d+)"));
    }

    private String extract(String input, String regex) {
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(regex).matcher(input);
        return m.find() ? m.group(1) : "";
    }

}
