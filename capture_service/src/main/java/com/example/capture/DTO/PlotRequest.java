package com.example.capture.DTO;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
public class PlotRequest {
    private List<String> headers;

}
