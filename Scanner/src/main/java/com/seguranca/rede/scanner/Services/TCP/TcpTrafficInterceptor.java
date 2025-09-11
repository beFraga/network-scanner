package com.seguranca.rede.scanner.Services.TCP;

import com.seguranca.rede.scanner.PacketInfo.TcpInfos;
import org.pcap4j.packet.IpPacket;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.util.NifSelector;
import org.pcap4j.core.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TcpTrafficInterceptor {

    private final BlockingQueue<TcpInfos> tcpQueue;

    public TcpTrafficInterceptor(BlockingQueue<TcpInfos> tcpQueue) {
        this.tcpQueue = tcpQueue;
    }

    static PcapNetworkInterface CapturarDispositvo() {
        // dispositivo de rede a ser analisado
        PcapNetworkInterface device = null;

        try {
            // lista os dispositivos de rede disponíveis com um prompt
            device = new NifSelector().selectNetworkInterface();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return device;

    }

    public void Scannear(int seconds) throws PcapNativeException, NotOpenException {
        PcapNetworkInterface device = CapturarDispositvo();
        System.out.println("Escolha : " + device);

        if (device == null) {
            System.out.println("Não existe dispositivo");
            System.exit(1);
        }

        int snapshotlenght = 65536;
        int readTimeout = 50;
        final PcapHandle handle;
        handle = device.openLive(snapshotlenght, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, readTimeout);

        PacketListener listener = new PacketListener() {
            @Override
            public void gotPacket(Packet packet){
                if (packet.contains(TcpPacket.class)) {
                    TcpPacket tcpPacket = packet.get(TcpPacket.class);
                    TcpInfos tcpinfos = new TcpInfos(packet.toString());
                    try {
                        tcpQueue.put(tcpinfos);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

            }
        };

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            try {
                handle.breakLoop();
            } catch (NotOpenException e) {
                e.printStackTrace();
            }
        }, seconds, TimeUnit.SECONDS);

        try {
            handle.loop(-1, listener); // -1 = captura "infinita"
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            handle.close();
            scheduler.shutdown();
        }
    }
}