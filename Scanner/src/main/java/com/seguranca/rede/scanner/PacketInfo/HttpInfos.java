package com.seguranca.rede.scanner.PacketInfo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
@Getter
@Setter
public class HttpInfos {
    private byte[] headerPayload;

    String remoteAddress;
    String localAddress;

    int remotePort;
    int localPort;

    String method;
    String path;
    String protocol;
    String uri;

    List<TcpInfos> tcpPackets = new ArrayList<>();

    public HttpInfos (HttpServletRequest req){
        this.remoteAddress = req.getRemoteAddr();
        this.localAddress = req.getLocalAddr();
        this.remotePort = req.getRemotePort();
        this.localPort = req.getLocalPort();
        this.method = req.getMethod();
        this.path = req.getPathInfo();
        this.uri = req.getRequestURI();
        this.protocol = req.getProtocol();
    }

    public void addTcpPacket(TcpInfos tcpPacket){
        this.tcpPackets.add(tcpPacket);
    }

}
