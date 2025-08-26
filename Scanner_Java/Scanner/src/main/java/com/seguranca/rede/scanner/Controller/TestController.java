package com.seguranca.rede.scanner.Controller;

import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.mapping.Collection;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
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

    @PostMapping("/catch_http")
    public Map<String, Object> capture(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        Map<String, Object> map = new HashMap<>();

        //Dados da rede
        map.put("remoteAddr", request.getRemoteAddr());
        map.put("remotePort", request.getRemotePort());
        map.put("localAddr", request.getLocalAddr());
        map.put("localPort", request.getLocalPort());

        //Dados HTTP
        map.put("method", request.getMethod());
        map.put("uri", request.getRequestURI());
        map.put("query", request.getQueryString());
        map.put("headers", Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(h -> h, h -> request.getHeader(h))));
        map.put("cookies", request.getCookies());

        //Json enviado pelo front
        map.put("body", body);

        return map;
    }
}
