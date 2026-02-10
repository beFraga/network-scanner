package com.example.authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
// 1. Makes spring find the class User in Common
@EntityScan(basePackages = {
        "com.example.common"
})
// 2. Makes spring find the repositories in Auth
@EnableJpaRepositories(basePackages = {
        "com.example.authentication.Repository"
})
public class AuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

}
