package com.example.capture.Capture.External;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class PythonPlotter {
    public static void generatePlot(List<String> headers) throws IOException, InterruptedException {

        ProcessBuilder pb = new ProcessBuilder(
                "C:/Users/famam/AppData/Local/Programs/Python/Python312/python.exe",
                "C:/Users/famam/IdeaProjects/network-scanner-javaml/plotter/main.py"
        );

        pb.directory(new File("C:/Users/famam/IdeaProjects/network-scanner-javaml/plotter"));

        // adiciona cada header como argumento do Python
        pb.command().addAll(headers);

        pb.redirectErrorStream(true);

        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[PYTHON] " + line);
            }
        }

        int exit = process.waitFor();
        if (exit != 0)
            throw new IOException("Python returned error code " + exit);
    }
}