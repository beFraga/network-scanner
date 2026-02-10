package com.example.capture.External;

import com.example.capture.Repository.TcpRepository;
import com.example.common.PacketInfo.HttpInfos;
import com.example.common.PacketInfo.TcpInfos;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import com.google.protobuf.Empty;

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
    private final PacketAnalysisServiceStub asyncStub; // Send stream
    private final PacketAnalyzerBlockingStub blockingStub; // Search responses

    public gRPCClient(TcpRepository tcpRepository, String host, int port) {
        this.tcpRepository = tcpRepository;
        // Create the communication channel with C++
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext() // No SSL to local development
                .build();

        this.asyncStub = PacketAnalysisServiceGrpc.newStub(channel);
        this.blockingStub = PacketAnalyzerGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    public void sendWindow(List<HttpInfos> packets) {
        final CountDownLatch finishLatch = new CountDownLatch(1);

        // Observer to receive the server response (Ack)
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

        // Start send streaming
        StreamObserver<PacketWindow> requestObserver = asyncStub.streamWindow(responseObserver);

        try {
            System.out.println("GRPC started:");
            // Build the Protobuf "PacketWindow" message
            PacketWindow.Builder windowBuilder = PacketWindow.newBuilder()
                    .setWindowStart(System.currentTimeMillis())
                    .setWindowEnd(System.currentTimeMillis())
                    .setUserId(1); // Example

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
                            .setPayloadSize(tcpInfos.getPayloadSize())
                            .build();
                    windowBuilder.addTcpPackets(meta);
                }
            }

            // Send the window to server
            requestObserver.onNext(windowBuilder.build());

            // Send ended
            requestObserver.onCompleted();

            // Wait to server to confirm
            finishLatch.await(2, TimeUnit.SECONDS);

        } catch (Exception e) {
            requestObserver.onError(e);
        }
    }

    public int getResults() {
        try {
            // call to server
            EventBatch batch = blockingStub.getEvents(Empty.newBuilder().build());
            int qtd = 0;

            System.out.println("Starting AI model");
            for (PacketEvent event : batch.getEventsList()) {
                TcpInfos tcpInfos = tcpRepository.getReferenceById(event.getId());
                tcpInfos.setFlag(event.getFlag());
                tcpRepository.save(tcpInfos);
                qtd++;
                System.out.println("packet " + qtd + " altered");
            }

            if (qtd > 0) {
                System.out.println("[gRPC] Received " + qtd + " results from C++.");
            }

            return qtd;

        } catch (Exception e) {
            System.err.println("[gRPC] Error while searching results: " + e.getMessage());
        }

        return 0;
    }
}