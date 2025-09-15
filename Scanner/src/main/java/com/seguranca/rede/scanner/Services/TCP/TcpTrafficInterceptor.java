package com.seguranca.rede.scanner.Services.TCP;

import com.seguranca.rede.scanner.PacketInfo.TcpInfos;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.util.NifSelector;
import org.pcap4j.core.*;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class TcpTrafficInterceptor {

    private final BlockingQueue<TcpInfos> tcpQueue;

    public TcpTrafficInterceptor(BlockingQueue<TcpInfos> tcpQueue) {
        this.tcpQueue = tcpQueue;
    }

    static PcapNetworkInterface CapturarDispositvo() {
        PcapNetworkInterface device = null;

        try {
            device = new NifSelector().selectNetworkInterface();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return device;

    }

    public void Scannear() throws PcapNativeException, NotOpenException {
        PcapNetworkInterface device = CapturarDispositvo();
        System.out.println("Escolha : " + device);

        if (device == null) {
            System.out.println("Não existe dispositivo");
            System.exit(1);
        }

        int snapshotlenght = 65536;
        int readTimeout = 1;
        PcapHandle handle = new PcapHandle.Builder(device.getName())
                .snaplen(snapshotlenght)
                .promiscuousMode(PcapNetworkInterface.PromiscuousMode.PROMISCUOUS)
                .timeoutMillis(readTimeout)
                .bufferSize(16 * 1024 * 1024) //16MB
                .immediateMode(true)
                .build();

        PacketListener listener = new PacketListener() {
            @Override
            public void gotPacket(Packet packet){
                if (packet.contains(TcpPacket.class)) {
                    TcpInfos tcpinfos = new TcpInfos(packet.toString(), packet);
                    try {
                        tcpQueue.put(tcpinfos);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };

        try {
            handle.loop(-1, listener); // -1 = captura "infinita"
        } catch (InterruptedException e) {
            e.getMessage();
        } finally {
            handle.close();
        }

        try {
            handle.breakLoop();
        } catch (NotOpenException e) {
            e.printStackTrace();
        }
    }
}