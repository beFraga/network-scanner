package com.example.authentication.DTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LoginRequest {
    private String username;
    private String email;
    private String password;
    private String code;
}
