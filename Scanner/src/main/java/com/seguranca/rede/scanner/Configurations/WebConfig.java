package com.seguranca.rede.scanner.Configurations;

import com.seguranca.rede.scanner.Services.HTTP.HttpTrafficInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final HttpTrafficInterceptor trafficInterceptor;

    @Autowired
    public WebConfig(HttpTrafficInterceptor trafficInterceptor) {
        this.trafficInterceptor = trafficInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(trafficInterceptor);
    }
}
