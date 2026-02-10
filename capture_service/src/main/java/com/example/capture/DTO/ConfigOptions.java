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
    @Min(value = 1, message = "The interval capture must be greater than 0")
    private Integer interval;

    @NotNull
    @Min(value = 1, message = "Context time must be greater than 0")
    private Integer tempCont;
}
