package com.seguranca.rede.scanner.PacketInfo;

import com.seguranca.rede.scanner.Controller.TestController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
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
    String remoteAddress;
    String localAddress;

    int remotePort;
    int localPort;

    String method;
    String path;
    String protocol;

    List<TcpInfos> tcpPackets = new ArrayList<>();

    public HttpInfos (HttpServletRequest req){
        this.remoteAddress = req.getRemoteAddr();
        this.localAddress = req.getLocalAddr();
        this.remotePort = req.getRemotePort();
        this.localPort = req.getLocalPort();
        this.method = req.getMethod();
        this.path = req.getPathInfo();
        this.protocol = req.getProtocol();
    }

    public void addTcpPacket(TcpInfos tcpPacket){
        this.tcpPackets.add(tcpPacket);
    }

}
