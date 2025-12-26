package com.example.authentication.Controller;


import com.example.authentication.DTO.AuthResponse;
import com.example.authentication.DTO.LoginRequest;
import com.example.authentication.Repository.UserRepository;
import com.example.authentication.Security.JwtUtil;
import com.example.authentication.User.TwoFactorService;
import com.example.common.PacketInfo.User;
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
    public ResponseEntity<?> createAccount(@RequestBody LoginRequest req) {
        Optional<User> opt = userRepository.findByEmail(req.getEmail());
        User user = new User();
        String hashPassword = passwordEncoder.encode(req.getPassword());
        if (opt.isPresent() && opt.get().isExists()) {
            return ResponseEntity.badRequest().body("email já registrado");
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


        return ResponseEntity.ok("Código de verificação será enviado!");
    }

    // Login inicial
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Optional<User> opt = userRepository.findByEmail(req.getEmail());
        if (opt.isEmpty() || !opt.get().isExists()) {
            return ResponseEntity.badRequest().body("Usuário não encontrado ou com falha de registro.");
        }
        User user = opt.get();
        if (!passwordEncoder.matches(req.getPassword(), user.getSenha())) {
            return ResponseEntity.status(401).body("Senha incorreta.");
        }
        // Gera e envia código 2FA
        twoFactorService.generateAndSendCode(user);
        return ResponseEntity.ok("Código de autenticação enviado para seu e-mail.");
    }

    // validação dos 2FA
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody LoginRequest req) {
        Optional<User> opt = userRepository.findByEmail(req.getEmail());
        if(opt.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        User user = opt.get();
        if (!user.isExists()) { // novo usuario
            boolean valid = twoFactorService.validateCode(user, req.getCode());
            if (!valid) {
                return ResponseEntity.status(401).body("Senha incorreta.");
            }
            user.setExists(true);
            userRepository.save(user);
        } else { // usuário já existente
            boolean valid = twoFactorService.validateCode(user, req.getCode());
            if (!valid) {
                return ResponseEntity.status(401).body("Código inválido ou expirado.");
            }
        }

        // Código válido → gera JWT
        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}