package com.seguranca.rede.scanner.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "two_factor_codes")
@Getter
@Setter
@NoArgsConstructor
public class TwoFactorCode {
    @Id @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "CDHASH")
    private String codeHash;

    @Column(name = "CREATED")
    private Timestamp createdAt;

    @Column(name = "EXPIRES")
    private Timestamp expiresAt;

    @Column(name = "ATTEMPTS")
    private int attempts = 0;

    @Column(name = "USED")
    private boolean used = false;
}