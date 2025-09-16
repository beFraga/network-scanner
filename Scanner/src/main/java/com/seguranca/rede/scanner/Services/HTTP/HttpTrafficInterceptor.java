package com.seguranca.rede.scanner.Services.HTTP;

import com.seguranca.rede.scanner.PacketInfo.HttpInfos;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.BlockingQueue;

@Service
public class HttpTrafficInterceptor implements HandlerInterceptor {

    private final BlockingQueue<HttpInfos> httpQueue;

    public HttpTrafficInterceptor(BlockingQueue<HttpInfos> httpQueue) {
        this.httpQueue = httpQueue;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){

        HttpInfos httpInfosRequest = new HttpInfos(request);
        try {
            httpQueue.put(httpInfosRequest);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
        return true;
    }

}
