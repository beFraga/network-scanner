package com.seguranca.rede.scanner.Repository;

import com.seguranca.rede.scanner.Model.PacketInfo.TcpInfos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TcpRepository extends JpaRepository <TcpInfos, Long> {
}
