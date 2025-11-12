package com.seguranca.rede.scanner.Controller;

import com.seguranca.rede.scanner.DTO.ConfigOptions;
import com.seguranca.rede.scanner.Model.UserInfo.User;
import com.seguranca.rede.scanner.Repository.HttpRepository;
import com.seguranca.rede.scanner.Repository.TcpRepository;
import com.seguranca.rede.scanner.Repository.UserRepository;
import com.seguranca.rede.scanner.Services.Capture.PacketCaptureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

// Só pode ser chamado se o JWT for válido

@RestController
@RequestMapping("/api/scanner")
@RequiredArgsConstructor
public class ScannerController {
    private final PacketCaptureService packetCaptureService;
    private final HttpRepository httpRepository;
    private final TcpRepository tcpRepository;
    private final UserRepository userRepository;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/start")
    public ResponseEntity<?> startCapture(@AuthenticationPrincipal User user) {
        try {
            packetCaptureService.startConnectPackets();
            packetCaptureService.schedulePrintTask(user.getInteravlo(), user);
            return ResponseEntity.ok("Captura de pacotes iniciada com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao iniciar captura: " + e.getMessage());
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/configure")
    public ResponseEntity<?> configure(@Valid @RequestBody ConfigOptions configOptions, BindingResult result, @AuthenticationPrincipal User user) {
        try{
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getAllErrors());
            }
            if (configOptions.getInterval() < 0 || configOptions.getTempCont() < 0){
                return ResponseEntity.badRequest().body("Valor não deve ser negativo");
            }
            user.setInteravlo(configOptions.getInterval());
            user.setTempoContexto(configOptions.getTempCont());

            userRepository.save(user);
            return ResponseEntity.ok("Configurações atualizadas com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao ataulizar configurações: " + e.getMessage());
        }
    }


}

