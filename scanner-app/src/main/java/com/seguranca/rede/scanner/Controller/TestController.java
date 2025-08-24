package com.seguranca.rede.scanner.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/test")
public class TestController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @PostMapping("/echo")
    public String echo(@RequestBody String body) {
        return "Recebi: " + body;
    }
}
