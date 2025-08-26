package com.seguranca.rede.scanner.Services;

import jakarta.servlet.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

public class CaptureFilter implements Filter {
    @Autowired
    private PacketCaptureService scanner;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        scanner.startCapture(5);
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
