package com.example.capture.DTO;

import lombok.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ConfigOptions {
    @NotNull
    @Min(value = 1, message = "O intervalo de captura deve ser maior que zero")
    private Integer interval;

    @NotNull
    @Min(value = 1, message = "O tempo de contexto deve ser maior que zero.")
    private Integer tempCont;
}
