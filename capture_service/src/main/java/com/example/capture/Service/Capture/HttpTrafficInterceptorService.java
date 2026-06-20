package com.example.capture.Service.Capture;

import com.example.common.PacketInfo.HttpInfos;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.BlockingQueue;

@Service
public class HttpTrafficInterceptorService implements HandlerInterceptor {

    private final BlockingQueue<HttpInfos> httpQueue;

    public HttpTrafficInterceptorService(BlockingQueue<HttpInfos> httpQueue) {
        this.httpQueue = httpQueue;
    }

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler){

        HttpInfos httpInfosRequest = new HttpInfos(request);
        try {
            httpQueue.put(httpInfosRequest);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
        return true;
    }

}
