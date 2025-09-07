package com.seguranca.rede.scanner.Services.HTTP;

import com.seguranca.rede.scanner.PacketInfo.HttpInfos;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class HttpTrafficInterceptor implements HandlerInterceptor {

    private final BlockingQueue<HttpInfos> httpQueue;

    public HttpTrafficInterceptor(BlockingQueue<HttpInfos> httpQueue) {
        this.httpQueue = httpQueue;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){

        HttpInfos httpInfos = new HttpInfos(request);
        System.out.println("Interceptando HTTP: " + httpInfos.getLocalAddress() + ":" + httpInfos.getLocalPort());

        try {
            httpQueue.put(httpInfos);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
        return true;
    }

}
