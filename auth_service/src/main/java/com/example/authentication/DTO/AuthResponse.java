package com.example.authentication.DTO;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String typetoken = "Bearer";
    private String email;
    private Boolean verifiedEmail;

    public AuthResponse(String token) {
        this.token = token;
    }
}
