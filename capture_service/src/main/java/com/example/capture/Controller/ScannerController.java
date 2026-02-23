package com.example.capture.Controller;

import com.example.capture.Capture.PacketCaptureService;
import com.example.capture.DTO.ConfigOptions;
import com.example.capture.DTO.PlotRequest;
import com.example.capture.Repository.UserRepository;
import com.example.common.UserInfo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
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

// Can only be called with a valid JWT
@RestController
@RequestMapping("/api/scanner")
@RequiredArgsConstructor
public class ScannerController {
    private final PacketCaptureService packetCaptureService;
    private final UserRepository userRepository;
    private final RestTemplateBuilder restTemplateBuilder;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/start")
    public ResponseEntity<?> startCapture(@AuthenticationPrincipal User user) {
        try {
            packetCaptureService.startConnectPackets();
            packetCaptureService.schedulePacketGathering(user.getInteravlo(), user);
            return ResponseEntity.ok("Packet capture started with success.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error while trying to start capture: " + e.getMessage());
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

    @Value("${PLOTTER_SERVICE_URL}")
    private String plotterUrl;

    @Value("${APP_INTERNAL_SECRET}")
    private String internalSecret;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/plot")
    public ResponseEntity<?> generatePythonPlots(@Valid @RequestBody PlotRequest dto) {
        try {
            // preparing headers to send to python
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Internal-Secret", internalSecret); // the secret key

            // creates the request
            HttpEntity<PlotRequest> requestEntity = new HttpEntity<>(dto, headers);

            // Print para debug
            System.out.println("Enviando para o Python: " + requestEntity.getBody().toString());
            System.out.println("Headers enviados: " + requestEntity.getHeaders());

            // send the POST to python
            ResponseEntity<String> response = restTemplateBuilder.build().postForEntity(
                    plotterUrl,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok("Plot generated with success");
            }
            return ResponseEntity.status(response.getStatusCode()).body("Pyton Error: " + response.getBody());
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Error while trying to generate plot: " + e.getMessage());
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
                return ResponseEntity.badRequest().body("Valor must not be negative");
            }
            user.setInteravlo(configOptions.getInterval());
            user.setTempoContexto(configOptions.getTempCont());

            userRepository.save(user);
            return ResponseEntity.ok("Settings altered with success.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error while trying to configure: " + e.getMessage());
        }
    }


}

