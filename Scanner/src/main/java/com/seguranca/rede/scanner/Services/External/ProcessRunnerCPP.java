package com.seguranca.rede.scanner.Services.External;

import com.seguranca.rede.scanner.Services.Capture.PacketCaptureService;

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
    private final PacketCaptureService packetCaptureService; //para atualizar as FLAGS dos pacotes

    public ProcessRunnerCPP(String projectPath, boolean useWSL, int intervalSeconds, PacketCaptureService packetCaptureService) {
            this.projectPath = projectPath;
            this.useWSL = useWSL;
            this.intervalSeconds = intervalSeconds;
            this.packetCaptureService = packetCaptureService;
    }

    public void runCppMakefile() throws IOException, InterruptedException {
        System.out.println("🚀 Iniciando execução periódica do modelo C++ a cada " + intervalSeconds + "s");

        // Primeiro comando: make train
        ProcessBuilder pbTrain = useWSL
                ? new ProcessBuilder("wsl", "make", "-C", projectPath, "train")
                : new ProcessBuilder("make", "-C", projectPath, "train");

        pbTrain.redirectErrorStream(true);
        System.out.println("🏋️ Rodando: make train");

        currentProcess = pbTrain.start();
        printProcessOutput(currentProcess);

        int exit1 = currentProcess.waitFor();
        if (exit1 != 0) {
            System.err.println("❌ Erro no make train (exit " + exit1 + ")");
            return;
        }

        System.out.println("✅ make train concluído.");
        scheduler.scheduleAtFixedRate(() -> {
            try {
                runCppModel();
            } catch (Exception e) {
                System.err.println("❌ Erro ao executar modelo C++: " + e.getMessage());
                e.printStackTrace();
            }
        }, 15, intervalSeconds, TimeUnit.SECONDS);
    }

    private void runCppModel() throws IOException, InterruptedException {
        if (currentProcess != null && currentProcess.isAlive()) {
            System.out.println("⚙️ Modelo C++ ainda em execução, aguardando próxima rodada...");
            return;
        }

        // Segundo comando: make
        ProcessBuilder pbExec = useWSL
                ? new ProcessBuilder("wsl", "make", "-C", projectPath)
                : new ProcessBuilder("make", "-C", projectPath);

        pbExec.redirectErrorStream(true);
        System.out.println("⚙️ Rodando: make");

        currentProcess = pbExec.start();
        printProcessOutput(currentProcess);
        int exit2 = currentProcess.waitFor();
        if (exit2 != 0) {
            System.err.println("❌ Erro no make (exit " + exit2 + ")");
        } else {
            System.out.println("🏁 Execução do modelo C++ concluída.");
            packetCaptureService.readJson();
        }
    }

    private void printProcessOutput(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[C++] " + line);
            }
        }
    }
}
