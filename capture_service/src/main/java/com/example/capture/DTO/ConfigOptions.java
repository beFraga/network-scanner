package com.example.capture.Capture.DTO;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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
