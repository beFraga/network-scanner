package com.example.capture.External;

import com.example.capture.Repository.TcpRepository;
import com.example.common.PacketInfo.HttpInfos;
import com.example.common.PacketInfo.TcpInfos;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import com.google.protobuf.Empty;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.example.capture.grpc.PacketAnalysisProto.*;
import com.example.capture.grpc.PacketAnalysisServiceGrpc;
import com.example.capture.grpc.PacketAnalyzerGrpc;

import com.example.capture.grpc.PacketAnalysisServiceGrpc.PacketAnalysisServiceStub;
import com.example.capture.grpc.PacketAnalyzerGrpc.PacketAnalyzerBlockingStub;

public class gRPCClient {

    private final TcpRepository tcpRepository;
    private final ManagedChannel channel;
    private final PacketAnalysisServiceStub asyncStub; // Para enviar stream (assíncrono)
    private final PacketAnalyzerBlockingStub blockingStub; // Para buscar respostas (síncrono)

    public gRPCClient(TcpRepository tcpRepository, String host, int port) {
        this.tcpRepository = tcpRepository;
        // Cria o canal de comunicação com o C++
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext() // Sem SSL para desenvolvimento local
                .build();

        this.asyncStub = PacketAnalysisServiceGrpc.newStub(channel);
        this.blockingStub = PacketAnalyzerGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /**
     * Envia uma lista de pacotes capturados para o C++ processar.
     * Substitui a escrita do arquivo JSON.
     */

    private String safe(String s) {
        return s == null ? "" : s;
    }

    public void sendWindow(List<HttpInfos> packets) {
        final CountDownLatch finishLatch = new CountDownLatch(1);

        // Observer para receber a resposta do servidor (Ack)
        StreamObserver<AnalysisAck> responseObserver = new StreamObserver<AnalysisAck>() {
            @Override
            public void onNext(AnalysisAck ack) {
                System.out.println("[gRPC] Ack recebido do C++: " + ack.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("[gRPC] Erro ao enviar: " + t.getMessage());
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                finishLatch.countDown();
            }
        };

        // Inicia o stream de envio
        StreamObserver<PacketWindow> requestObserver = asyncStub.streamWindow(responseObserver);

        try {
            System.out.println("Começou GRPC:");
            // Constrói a mensagem Protobuf "PacketWindow"
            PacketWindow.Builder windowBuilder = PacketWindow.newBuilder()
                    .setWindowStart(System.currentTimeMillis())
                    .setWindowEnd(System.currentTimeMillis())
                    .setUserId(1); // Exemplo

            System.out.println("[gRPC] Pacotes http recebidos para envio: " + packets.size());
            System.out.println("[gRPC] Pacotes tcp de um dos https: " + packets.get(0).getTcpPackets().size());
            for (int i = 0; i < packets.size(); i++) {
                for (TcpInfos tcpInfos : packets.get(i).getTcpPackets()){
                    // Converte cada pacote Java para TcpMeta do Protobuf
                    TcpMeta meta = TcpMeta.newBuilder()
                            .setMethod(safe(tcpInfos.getHttpInfos().getMethod()))
                            .setProtocol(safe(tcpInfos.getHttpInfos().getProtocol()))
                            .setId(tcpInfos.getId())
                            .setLocalAddress(safe(tcpInfos.getLocalAddress()))
                            .setRemoteAddress(safe(tcpInfos.getRemoteAddress()))
                            .setRemotePort(tcpInfos.getRemotePort())
                            .setSequenceNumber(tcpInfos.getSequenceNumber())
                            .setPayloadSize(
                                    tcpInfos.getPayload() == null ? 0 : tcpInfos.getPayload().length()
                            )
                            .build();
                    windowBuilder.addTcpPackets(meta);
                    System.out.println("pacote " + i + " enviado corretamente");
                }
            }

            // Envia a janela para o servidor
            requestObserver.onNext(windowBuilder.build());
            System.out.println("janela enviada");

            // Finaliza o envio
            requestObserver.onCompleted();
            System.out.println("envio finalizado");

            // Espera o servidor confirmar (opcional, mas bom para garantir sincronia)
            finishLatch.await(2, TimeUnit.SECONDS);
            System.out.println("servidor confirmado");

        } catch (Exception e) {
            requestObserver.onError(e);
        }
    }

    /**
     * Busca os resultados processados (Outliers).
     * Substitui a leitura do JSON de resposta.
     */
    public int getResults() {
        try {
            // Chamada síncrona simples
            EventBatch batch = blockingStub.getEvents(Empty.newBuilder().build());
            int qtd = 0;

            System.out.println("Começando modelo ia");
            for (PacketEvent event : batch.getEventsList()) {
                TcpInfos tcpInfos = tcpRepository.getReferenceById(event.getId());
                tcpInfos.setFlag(event.getFlag());
                tcpRepository.save(tcpInfos);
                qtd++;
                System.out.println("pacote " + qtd + " alterado");
            }

            if (qtd > 0) {
                System.out.println("[gRPC] Recebidos " + qtd + " resultados do C++.");
            }

            return qtd;

        } catch (Exception e) {
            System.err.println("[gRPC] Erro ao buscar resultados: " + e.getMessage());
        }

        return 0;
    }
}