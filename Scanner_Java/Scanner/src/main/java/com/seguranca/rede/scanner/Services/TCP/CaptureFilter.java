package com.seguranca.rede.scanner.Services.TCP;

import jakarta.servlet.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class CaptureFilter implements Filter {
    @Autowired
    private PacketCaptureService scanner;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
