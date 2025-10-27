package com.seguranca.rede.scanner.Controller;

import com.seguranca.rede.scanner.Configurations.QueueConfig;
import com.seguranca.rede.scanner.Model.User;
import com.seguranca.rede.scanner.Repository.HttpRepository;
import com.seguranca.rede.scanner.Repository.TcpRepository;
import com.seguranca.rede.scanner.Services.Capture.PacketCaptureService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

// Só pode ser chamado se o JWT for válido

@RestController
@RequestMapping("/api/scanner")
@RequiredArgsConstructor
public class ScannerController {
    private final PacketCaptureService packetCaptureService;
    private final HttpRepository httpRepository;
    private final TcpRepository tcpRepository;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/start")
    public ResponseEntity<?> startCapture(@AuthenticationPrincipal User user) {
        try {
            int interval;
            if (Objects.equals(user.getEmail(), "arthurmegapower3@gmail.com")) {
                interval = 10;
            } else {
                interval = user.getInteravlo();
            }
            packetCaptureService.startConnectPackets();
            packetCaptureService.schedulePrintTask(interval, user);
            return ResponseEntity.ok("Captura de pacotes iniciada com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao iniciar captura: " + e.getMessage());
        }
    }
}

