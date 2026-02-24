package com.example.authentication.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
public class LoginRequest {
    @NotBlank(message = "Username must be filled")
    private String username;

    @NotBlank(message = "Email must be filled")
    private String email;

    @NotBlank(message = "Password must be filled")
    private String password;
}
