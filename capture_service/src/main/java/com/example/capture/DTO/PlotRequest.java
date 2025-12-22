package com.example.capture.Capture.DTO;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
public class PlotRequest {
    private List<String> headers;

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }
}
