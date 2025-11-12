package com.seguranca.rede.scanner.Services.ML;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ProcessRunner {

    /**
     * Executa o makefile e deixa o processo rodando em paralelo.
     *
     * @param makeDirectory Caminho da pasta onde está o Makefile
     */
    public void runCppMakefile(String makeDirectory) {
        try {
            ProcessBuilder builder = new ProcessBuilder("make");
            builder.directory(new java.io.File(makeDirectory));
            builder.redirectErrorStream(true); // junta stderr e stdout

            Process process = builder.start();

            // Thread para ler a saída do processo
            Thread outputThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[C++] " + line);
                    }
                } catch (Exception e) {
                    System.err.println("❌ Erro lendo saída do processo: " + e.getMessage());
                }
            });

            outputThread.start();

            System.out.println("🚀 Makefile iniciado em: " + makeDirectory);

            // Opcional: não bloqueia, o processo fica rodando em paralelo
            // Se quiser esperar terminar, descomente:
            // int exitCode = process.waitFor();
            // System.out.println("✅ Processo finalizado com código: " + exitCode);

        } catch (Exception e) {
            System.err.println("❌ Erro ao executar makefile: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
