package com.example.capture.Controller;

import com.example.capture.Capture.PacketCaptureService;
import com.example.capture.DTO.ConfigOptions;
import com.example.capture.DTO.PlotRequest;
import com.example.capture.External.PythonPlotter;
import com.example.capture.Repository.UserRepository;
import com.example.common.UserInfo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

// Só pode ser chamado se o JWT for válido

@RestController
@RequestMapping("/api/scanner")
@RequiredArgsConstructor
public class ScannerController {
    private final PacketCaptureService packetCaptureService;
    private final UserRepository userRepository;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/start")
    public ResponseEntity<?> startCapture(@AuthenticationPrincipal User user) {
        try {
            packetCaptureService.startConnectPackets();
            packetCaptureService.schedulePacketGathering(user.getInteravlo(), user);
            return ResponseEntity.ok("Captura de pacotes iniciada com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao iniciar captura: " + e.getMessage());
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/manual")
    public ResponseEntity<byte[]> getManual() throws IOException {
        Path path = Paths.get("user_manual.pdf").normalize().toAbsolutePath();
        byte[] bytes = Files.readAllBytes(path);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(bytes.length);

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/plot")
    public ResponseEntity<?> generatePythonPlots(@RequestBody PlotRequest dto) {

        try {
            List<String> headers = dto.getHeaders();

            // chama o plotter Python
            String path = Paths.get("plotter").toAbsolutePath().normalize().toString();
            PythonPlotter.generatePlot(headers, path);

            return ResponseEntity.ok("Plot(s) gerado(s) com sucesso!");
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao gerar plot: " + e.getMessage());
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

