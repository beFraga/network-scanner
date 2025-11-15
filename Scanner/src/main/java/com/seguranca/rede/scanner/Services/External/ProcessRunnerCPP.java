package com.seguranca.rede.scanner.Services.External;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProcessRunnerCPP {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final String projectPath;     // Caminho do projeto C++ (onde está o Makefile)
    private final boolean useWSL;         // true = rodar via WSL, false = Linux nativo
    private final int intervalSeconds;    // intervalo de execução
    private Process currentProcess;       // referência para o processo atual

    public ProcessRunnerCPP(String projectPath, boolean useWSL, int intervalSeconds) {
            this.projectPath = projectPath;
            this.useWSL = useWSL;
            this.intervalSeconds = intervalSeconds;
        }

    public void runCppMakefile() {
        System.out.println("🚀 Iniciando execução periódica do modelo C++ a cada " + intervalSeconds + "s");

        scheduler.scheduleAtFixedRate(() -> {
            try {
                runCppModel();
            } catch (Exception e) {
                System.err.println("❌ Erro ao executar modelo C++: " + e.getMessage());
                e.printStackTrace();
            }
        }, 0, intervalSeconds, TimeUnit.SECONDS);
    }

    private void runCppModel() throws IOException, InterruptedException {
        // Caso o modelo já esteja rodando, não inicia outro
        if (currentProcess != null && currentProcess.isAlive()) {
            System.out.println("⚙️ Modelo C++ ainda em execução, aguardando próxima rodada...");
            return;
        }

        // Monta comando de execução
        ProcessBuilder pb;
        if (useWSL) {
            pb = new ProcessBuilder("wsl", "make", "-C", projectPath);
        } else {
            pb = new ProcessBuilder("make", "-C", projectPath);
        }

        pb.redirectErrorStream(true); // mistura stderr com stdout
        currentProcess = pb.start();

        // Captura logs do processo
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(currentProcess.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[C++] " + line);
            }
        }

        int exitCode = currentProcess.waitFor();
        if (exitCode == 0) {
            System.out.println("✅ Execução do modelo C++ concluída com sucesso.");
        } else {
            System.err.println("⚠️ Modelo C++ terminou com código: " + exitCode);
        }
    }

    public void stop() {
        scheduler.shutdownNow();
        if (currentProcess != null && currentProcess.isAlive()) {
            currentProcess.destroy();
        }
        System.out.println("🛑 Execução do modelo C++ encerrada.");
    }
}
