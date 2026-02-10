package com.example.capture.External;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class PythonPlotter {
    public static void generatePlot(List<String> headers, String path) throws IOException, InterruptedException {

        String pythonCommand = System.getProperty("os.name").toLowerCase().contains("win")
                ? "python"
                : "python3";

        ProcessBuilder pb = new ProcessBuilder(
                pythonCommand,
                Paths.get("plotter/main.py").toAbsolutePath().normalize().toString()
        );

        List<String> command = new ArrayList<>();
        command.add("python3");

        pb.directory(new File(path));

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