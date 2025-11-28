package com.seguranca.rede.scanner.Repository;

import com.seguranca.rede.scanner.Model.UserInfo.TwoFactorCode;
import com.seguranca.rede.scanner.Model.UserInfo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TwoFactorCodeRepository extends JpaRepository<TwoFactorCode, Long> {
    List<TwoFactorCode> findTopByUserAndUsedFalseOrderByCreatedAtDesc(User user);
}