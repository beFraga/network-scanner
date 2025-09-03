package com.seguranca.rede.scanner.PacketInfo;

import com.seguranca.rede.scanner.Controller.TestController;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@AllArgsConstructor
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
    String cookie;

}
