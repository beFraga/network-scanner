package com.seguranca.rede.scanner.PacketInfo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
public class TCPinfos {
    String localAdress;
    String remoteAdress;
}
