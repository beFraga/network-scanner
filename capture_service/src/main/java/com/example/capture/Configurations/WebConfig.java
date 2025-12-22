package com.example.capture.Capture.Configurations;

import com.example.capture.Capture.Capture.HttpTrafficInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final HttpTrafficInterceptor trafficInterceptor;
    private QueueConfig queue;

    @Bean
    public HttpTrafficInterceptor trafficInterceptor() {
        return new HttpTrafficInterceptor(queue.httpQueue());
    }

    @Autowired
    public WebConfig(HttpTrafficInterceptor trafficInterceptor) {
        this.trafficInterceptor = trafficInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(trafficInterceptor);
    }
}
