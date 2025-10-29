package com.seguranca.rede.scanner.Repository;

import com.seguranca.rede.scanner.Model.PacketInfo.HttpInfos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HttpRepository extends JpaRepository<HttpInfos, Long> {
}
