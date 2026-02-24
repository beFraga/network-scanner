package com.example.capture.Configurations;

import com.example.capture.Capture.HttpTrafficInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final HttpTrafficInterceptor trafficInterceptor;
    private final QueueConfig queue;

    @Bean
    public HttpTrafficInterceptor trafficInterceptor() {
        return new HttpTrafficInterceptor(queue.httpQueue());
    }

    @Autowired
    public WebConfig(HttpTrafficInterceptor trafficInterceptor, QueueConfig queue) {
        this.trafficInterceptor = trafficInterceptor;
        this.queue = queue;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(trafficInterceptor);
    }
}
