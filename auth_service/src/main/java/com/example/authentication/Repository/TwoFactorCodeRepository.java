package com.example.authentication.Repository;


import com.example.authentication.TwoFactor.TwoFactorCode;
import com.example.common.UserInfo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TwoFactorCodeRepository extends JpaRepository<TwoFactorCode, Long> {
    List<TwoFactorCode> findTopByUserAndUsedFalseOrderByCreatedAtDesc(User user);
}