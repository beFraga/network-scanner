package com.example.capture.Controller;

import com.example.capture.Service.CaptureService;
import com.example.capture.DTO.ConfigOptionsRequest;
import com.example.capture.DTO.PlotRequest;
import com.example.common.UserInfo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.IOException;

// Can only be called with a valid JWT
@RestController
@RequestMapping("/api/scanner")
@RequiredArgsConstructor
public class ScannerController {
    private final CaptureService captureService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/start")
    public ResponseEntity<?> startCapture(@AuthenticationPrincipal User user) {
        return captureService.startCapture(user);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/manual")
    public ResponseEntity<byte[]> getManual() throws IOException {
        return captureService.getManual();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/plot")
    public ResponseEntity<?> generatePythonPlots(@Valid @RequestBody PlotRequest dto) {
        return captureService.generatePythonPlots(dto);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/configure")
    public ResponseEntity<?> configure(@Valid @RequestBody ConfigOptionsRequest configOptionsRequest, BindingResult result, @AuthenticationPrincipal User user) {
        return captureService.configure(configOptionsRequest, result, user);
    }

}

