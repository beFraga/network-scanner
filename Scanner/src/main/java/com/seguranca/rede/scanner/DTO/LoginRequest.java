package com.seguranca.rede.scanner.DTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LoginRequest {
    private String email;
    private String password;
    private String code;
}
