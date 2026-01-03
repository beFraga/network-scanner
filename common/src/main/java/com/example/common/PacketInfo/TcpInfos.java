package com.example.common.PacketInfo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pcap4j.packet.*;

@Entity
@Table(name = "tcp_infos")
@Getter
@Setter
@NoArgsConstructor
public class TcpInfos {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "local_address")
    private String localAddress;

    @Column(name = "remote_address")
    private String remoteAddress;

    @Column(name = "local_port")
    private int localPort;

    @Column(name = "remote_port")
    private int remotePort;

    @Column(name = "sequence_number")
    private Long sequenceNumber;

    @Lob
    private Packet payload;

    @ManyToOne
    @JoinColumn(name = "http_infos_id")
    private HttpInfos httpInfos;

    @Column(name = "FLAG", nullable = false)
    private Boolean flag;

    public TcpInfos(IpPacket ipPacket, TcpPacket tcpPacket){
        this.localAddress = ipPacket.getHeader().getSrcAddr().getHostAddress();
        this.remoteAddress = ipPacket.getHeader().getDstAddr().getHostAddress();
        this.localPort = tcpPacket.getHeader().getSrcPort().valueAsInt();
        this.remotePort = tcpPacket.getHeader().getDstPort().valueAsInt();
        this.sequenceNumber = Long.valueOf(tcpPacket.getHeader().getSequenceNumber());
        this.payload = tcpPacket.getPayload();
        this.flag = false;
    }

    public TcpInfos(long id, boolean flag, String remoteAddress, HttpInfos httpInfos){
        this.id = id;
        this.flag = flag;
        this.remoteAddress = remoteAddress;
        this.localAddress = null;
        this.localPort = 0;
        this.remotePort = 0;
        this.sequenceNumber = null;
        this.payload = null;
        this.httpInfos = httpInfos;
    }

    public TcpInfos(long id, boolean flag, String remoteAddress){
        this.id = id;
        this.flag = flag;
        this.remoteAddress = remoteAddress;
        this.localAddress = null;
        this.localPort = 0;
        this.remotePort = 0;
        this.sequenceNumber = null;
        this.payload = null;
    }

}
