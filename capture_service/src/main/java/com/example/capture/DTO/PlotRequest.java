package com.example.capture.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Data
@Getter
@ToString
public class PlotRequest {
    @NotEmpty(message = "Define the elements.")
    private List<String> headers;

    @NotBlank(message = "Define the start.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Invalid format. Use AAAA-MM-DD")
    private String start_date;

    @NotBlank(message = "Define the end.")
    private String end_date;
}
