package com.seguranca.rede.scanner.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/test")
public class TestController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/catch_http")
    public void capture(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> body) {}
}
