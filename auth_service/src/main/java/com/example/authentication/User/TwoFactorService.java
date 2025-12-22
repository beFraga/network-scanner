package com.example.authentication.User;

import com.example.common.PacketInfo.*;
import com.example.authentication.Repository.TwoFactorCodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
public class TwoFactorService {
    private final TwoFactorCodeRepository codeRepo;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder; // BCrypt

    public void generateAndSendCode(User user) {
        String code = generateNumericCode(6);
        String hashed = passwordEncoder.encode(code);

        TwoFactorCode tf = new TwoFactorCode();
        tf.setUser(user);
        tf.setCodeHash(hashed);
        tf.setCreatedAt(Timestamp.from(Instant.now()));
        tf.setExpiresAt(Timestamp.from(Instant.now().plus(5, ChronoUnit.MINUTES))); // 5 min
        codeRepo.save(tf);

        sendEmail(user.getEmail(), "Seu código de autenticação", "Código: " + code + "\nValidade: 5 minutos");
    }

    public boolean validateCode(User user, String code) {
        List<TwoFactorCode> list = codeRepo.findTopByUserAndUsedFalseOrderByCreatedAtDesc(user);
        if (list.isEmpty()) return false;
        TwoFactorCode tf = list.get(0);
        if (tf.getExpiresAt().before(new Timestamp(System.currentTimeMillis()))) return false;
        if (tf.getAttempts() >= 5) return false; // proteção contra força bruta
        boolean matches = passwordEncoder.matches(code, tf.getCodeHash());
        tf.setAttempts(tf.getAttempts() + 1);
        if (matches) {
            tf.setUsed(true);
        }
        codeRepo.save(tf);
        return matches;
    }

    private String generateNumericCode(int digits) {
        SecureRandom rnd = new SecureRandom();
        int max = (int)Math.pow(10, digits);
        int n = rnd.nextInt(max - (max/10)) + (max/10); // garante dígitos
        return String.format("%0" + digits + "d", n);
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }
}