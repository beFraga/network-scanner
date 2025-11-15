package com.seguranca.rede.scanner.Services.External;

import org.springframework.stereotype.Service;
// ... (outros imports)
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class PythonPlotter {

    private static final String scriptPath = "C:\\Users\\famam\\IdeaProjects\\network-scanner-javaml\\test.py";
    private static final String pythonCommand = "python";

    public static byte[] generatePlot(int graphId) throws IOException, InterruptedException {

        Path tempPlotFile = null;
        String graphIdString = String.valueOf(graphId); // Converte o int para String para passar para o SO

        try {
            tempPlotFile = Files.createTempFile("plot_", ".png");

            // 1. O ProcessBuilder agora recebe 3 argumentos:
            // "python", "[caminho_script.py]", "[caminho_saida.png]", "[ID_GRAFICO]"
            ProcessBuilder pb = new ProcessBuilder(
                    pythonCommand,
                    scriptPath,
                    tempPlotFile.toAbsolutePath().toString(),
                    graphIdString // argumento para o Python (seleção de gráficos)
            );

            // ... (Restante do ProcessBuilder e lógica de execução permanece o mesmo) ...
            pb.redirectErrorStream(true);

            System.out.println("Executando script Python: " + String.join(" ", pb.command()));
            Process process = pb.start();

            // Lógica de leitura de logs (removida para brevidade, mas deve ser mantida)
            // ...

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                // Se o script falhou, mostra os logs de erro
                throw new IOException("Script Python falhou. Codigo: " + exitCode + ". Verifique o log PY_SCRIPT_LOG.");
            }

            // Lê os bytes da imagem
            return Files.readAllBytes(tempPlotFile);

        } finally {
            // Limpa o arquivo temporário
            if (tempPlotFile != null) {
                try {
                    Files.delete(tempPlotFile);
                } catch (IOException e) {
                    System.err.println("Aviso: Falha ao deletar arquivo temporario: " + tempPlotFile);
                }
            }
        }
    }
}