package com.example.common.PacketInfo;

import com.seguranca.rede.scanner.auth_service.src.UserInfo.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "http_infos")
@NoArgsConstructor
@Getter
@Setter
public class HttpInfos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private byte[] headerPayload;
    @Transient
    private String remoteAddress;
    @Transient
    private String localAddress;
    @Transient
    private int remotePort;
    @Transient
    private int localPort;

    private String method;
    private String protocol;
    private String uri;

    @OneToMany(mappedBy = "httpInfos", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TcpInfos> tcpPackets = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public HttpInfos (HttpServletRequest req){
        this.remoteAddress = req.getRemoteAddr();
        this.localAddress = req.getLocalAddr();
        this.remotePort = req.getRemotePort();
        this.localPort = req.getLocalPort();
        this.method = req.getMethod();
        this.uri = req.getRequestURI();
        this.protocol = req.getProtocol();
    }

    public void addTcpPacket(TcpInfos tcpPacket){
        this.tcpPackets.add(tcpPacket);
    }

}
