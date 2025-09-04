package com.seguranca.rede.scanner.Services.HTTP;

import com.seguranca.rede.scanner.PacketInfo.HttpInfos;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

@Service
public class HttpTrafficInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        System.out.println("interceptando: " + request.getRequestURI());

        HttpInfos http =  new HttpInfos(request);
        return true;
    }

}
