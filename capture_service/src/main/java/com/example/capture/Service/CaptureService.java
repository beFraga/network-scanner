package com.example.capture.Service;

import com.example.capture.DTO.ConfigOptionsRequest;
import com.example.capture.DTO.PlotRequest;
import com.example.capture.Repository.UserRepository;
import com.example.capture.Service.Capture.PacketCaptureService;
import com.example.common.UserInfo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class CaptureService {
    private final PacketCaptureService packetCaptureService;
    private final UserRepository userRepository;
    private final RestTemplateBuilder restTemplateBuilder;

    @Value("${PLOTTER_SERVICE_URL}")
    private String plotterUrl;

    @Value("${APP_INTERNAL_SECRET}")
    private String internalSecret;

    public ResponseEntity<?> startCapture(User user) {
        try {
            packetCaptureService.startConnectPackets();
            packetCaptureService.schedulePacketGathering(user.getInteravlo(), user);
            return ResponseEntity.ok("Packet capture started with success.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error while trying to start capture: " + e.getMessage());
        }
    }

    public ResponseEntity<byte[]> getManual() throws IOException {
        Path path = Paths.get("user_manual.pdf").normalize().toAbsolutePath();
        byte[] bytes = Files.readAllBytes(path);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(bytes.length);

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    public ResponseEntity<?> generatePythonPlots(PlotRequest dto) {
        try {
            // preparing headers to send to python
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Internal-Secret", internalSecret); // the secret key

            // creates the request
            HttpEntity<PlotRequest> requestEntity = new HttpEntity<>(dto, headers);

            // Print para debug
            assert requestEntity.getBody() != null;
            System.out.println("Enviando para o Python: " + requestEntity.getBody());
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

    public ResponseEntity<?> configure(ConfigOptionsRequest configOptionsRequest, BindingResult result, User user) {
        try{
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getAllErrors());
            }
            if (configOptionsRequest.getInterval() < 0 || configOptionsRequest.getTempCont() < 0){
                return ResponseEntity.badRequest().body("Valor must not be negative");
            }
            user.setInteravlo(configOptionsRequest.getInterval());
            user.setTempoContexto(configOptionsRequest.getTempCont());

            userRepository.save(user);
            return ResponseEntity.ok("Settings altered with success.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error while trying to configure: " + e.getMessage());
        }
    }
}
