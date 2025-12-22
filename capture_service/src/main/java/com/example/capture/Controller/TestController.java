package com.example.capture.Controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/test")
public class TestController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/catch_http")
    public void capture() {}
}
