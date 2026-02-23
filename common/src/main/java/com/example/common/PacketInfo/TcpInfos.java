package com.example.common.PacketInfo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pcap4j.packet.*;

import java.sql.Timestamp;
import java.time.Instant;

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
    @Transient
    private byte[] payloadRaw;

    @Column(name = "payload")
    private Long payloadSize;

    @Column(name = "received_at")
    private Timestamp received_at;

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
        this.sequenceNumber = (long) tcpPacket.getHeader().getSequenceNumber();
        this.payloadRaw = (tcpPacket.getPayload() != null)
                ? tcpPacket.getPayload().getRawData()
                : new byte[0];
        this.payloadSize = (long) payloadRaw.length;
        this.received_at = Timestamp.from(Instant.now());
        this.flag = false;
    }
}
