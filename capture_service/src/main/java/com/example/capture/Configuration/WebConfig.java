package com.example.capture.Configuration;

import com.example.capture.Service.Capture.HttpTrafficInterceptorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final HttpTrafficInterceptorService trafficInterceptor;
    private final QueueConfig queue;

    @Bean
    public HttpTrafficInterceptorService trafficInterceptor() {
        return new HttpTrafficInterceptorService(queue.httpQueue());
    }

    @Autowired
    public WebConfig(HttpTrafficInterceptorService trafficInterceptor, QueueConfig queue) {
        this.trafficInterceptor = trafficInterceptor;
        this.queue = queue;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(trafficInterceptor);
    }
}
