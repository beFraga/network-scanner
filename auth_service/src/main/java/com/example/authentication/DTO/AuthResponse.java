package com.example.authentication.DTO;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String typetoken = "Bearer";
    private String email;
    private Boolean verifiedEmail;

    public AuthResponse(String token, String email, Boolean verifiedEmail) {
        this.token = token;
        this.email = email;
        this.verifiedEmail = verifiedEmail;
    }
}
