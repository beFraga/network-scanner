package com.example.capture.Capture.Repository;

import com.example.common.PacketInfo.HttpInfos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HttpRepository extends JpaRepository<HttpInfos, Long> {
}
