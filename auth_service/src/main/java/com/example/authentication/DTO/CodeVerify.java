package com.example.authentication.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
public class CodeVerify {
    @NotBlank(message = "Email must be filled.")
    private String email;

    @NotBlank(message = "Code must be filled")
    private String code;
}
