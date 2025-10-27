package com.seguranca.rede.scanner.Repository;

import com.seguranca.rede.scanner.Model.TwoFactorCode;
import com.seguranca.rede.scanner.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TwoFactorCodeRepository extends JpaRepository<TwoFactorCode, Long> {
    List<TwoFactorCode> findByUserAndUsedFalseOrderByCreatedAtDesc(User user);

    Optional<TwoFactorCode> findFirstByUserAndUsedFalseOrderByCreatedAtDesc(User user);

    List<TwoFactorCode> findTopByUserAndUsedFalseOrderByCreatedAtDesc(User user);
}