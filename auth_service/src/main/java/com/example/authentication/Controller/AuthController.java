package com.example.authentication.Controller;


import com.example.authentication.DTO.AuthResponse;
import com.example.authentication.DTO.CodeVerifyRequest;
import com.example.authentication.DTO.LoginRequest;
import com.example.authentication.Repository.UserRepository;
import com.example.authentication.Security.JwtUtil;
import com.example.authentication.Service.TwoFactorService;
import com.example.authentication.Service.UserService;
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

    private final UserService userService;

    // Create account
    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@Valid @RequestBody LoginRequest req) {
        return userService.createAccount(req);
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        return userService.login(req);
    }

    // 2FA
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody CodeVerifyRequest req) {
        return userService.verifyCode(req);
    }

}