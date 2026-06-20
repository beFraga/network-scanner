package com.example.authentication.Service;

import com.example.authentication.DTO.AuthResponse;
import com.example.authentication.DTO.CodeVerifyRequest;
import com.example.authentication.DTO.LoginRequest;
import com.example.authentication.Repository.UserRepository;
import com.example.authentication.Security.JwtUtil;
import com.example.common.UserInfo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TwoFactorService twoFactorService;
    private final JwtUtil jwtUtil;

    public ResponseEntity<?> createAccount(LoginRequest req) {
        Optional<User> opt = userRepository.findByEmail(req.getEmail());
        User user = new User();
        String hashPassword = passwordEncoder.encode(req.getPassword());
        if (opt.isPresent() && opt.get().isExists()) {
            return ResponseEntity.badRequest().body("email already registred");
        } else if (opt.isPresent()) {
            opt.get().setSenha(hashPassword);
            opt.get().setCreatedAt(Timestamp.from(Instant.now()));
            userRepository.save(opt.get());
            twoFactorService.generateAndSendCode(opt.get());
        } else {
            user.setExists(false);
            user.setUsername(req.getEmail());
            user.setEmail(req.getEmail());
            user.setSenha(hashPassword);
            user.setCreatedAt(Timestamp.from(Instant.now()));
            user.setInteravlo(600);
            userRepository.save(user);
            twoFactorService.generateAndSendCode(user);
        }

        return ResponseEntity.ok("Verification code will be sent!");
    }

    public ResponseEntity<?> login(LoginRequest req) {
        Optional<User> opt = userRepository.findByEmail(req.getEmail());
        if (opt.isEmpty() || !opt.get().isExists()) {
            return ResponseEntity.badRequest().body("User not found or register fail.");
        }
        User user = opt.get();
        if (!passwordEncoder.matches(req.getPassword(), user.getSenha())) {
            return ResponseEntity.status(401).body("Incorrect password.");
        }
        // Generates and sends 2FA code
        twoFactorService.generateAndSendCode(user);
        return ResponseEntity.ok("Auth code will be sent to your email.");
    }

    public ResponseEntity<?> verifyCode(CodeVerifyRequest req) {
        Optional<User> opt = userRepository.findByEmail(req.getEmail());
        if(opt.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        User user = opt.get();
        boolean valid = twoFactorService.validateCode(user, req.getCode());
        if (!user.isExists()) { // new user
            if (!valid) {
                return ResponseEntity.status(401).body("Incorrect password.");
            }
            user.setExists(true);
            userRepository.save(user);
        } else { // already existing user
            if (!valid) {
                return ResponseEntity.status(401).body("Invalid or expired code.");
            }
        }

        // Valid code and generates JWT
        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), true));
    }

}
