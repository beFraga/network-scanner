package com.example.authentication.Controller;


import com.example.authentication.DTO.AuthResponse;
import com.example.authentication.DTO.CodeVerify;
import com.example.authentication.DTO.LoginRequest;
import com.example.authentication.Repository.UserRepository;
import com.example.authentication.Security.JwtUtil;
import com.example.authentication.TwoFactor.TwoFactorService;
import com.example.common.UserInfo.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TwoFactorService twoFactorService;
    private final JwtUtil jwtUtil;

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@Valid @RequestBody LoginRequest req) {
        Optional<User> opt = userRepository.findByEmail(req.getEmail());
        User user = new User();
        String hashPassword = passwordEncoder.encode(req.getPassword());
        if (opt.isPresent() && opt.get().isExists()) {
            return ResponseEntity.badRequest().body("email already registred");
        } else if (opt.isPresent() && !opt.get().isExists()) {
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

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
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

    // 2FA
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody CodeVerify req) {
        Optional<User> opt = userRepository.findByEmail(req.getEmail());
        if(opt.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        User user = opt.get();
        if (!user.isExists()) { // new user
            boolean valid = twoFactorService.validateCode(user, req.getCode());
            if (!valid) {
                return ResponseEntity.status(401).body("Incorrect password.");
            }
            user.setExists(true);
            userRepository.save(user);
        } else { // already existing user
            boolean valid = twoFactorService.validateCode(user, req.getCode());
            if (!valid) {
                return ResponseEntity.status(401).body("Invalid or expired code.");
            }
        }

        // Valid code → generates JWT
        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}