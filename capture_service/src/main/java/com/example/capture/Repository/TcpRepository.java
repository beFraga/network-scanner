package com.example.capture.Capture.Repository;

import com.example.common.PacketInfo.TcpInfos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TcpRepository extends JpaRepository <TcpInfos, Long> {
}
