package com.seguranca.rede.scanner.Services.TCP;

import com.seguranca.rede.scanner.PacketInfo.TcpInfos;
import org.pcap4j.packet.*;
import org.pcap4j.util.NifSelector;
import org.pcap4j.core.*;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class TcpTrafficInterceptor {

    private final BlockingQueue<TcpInfos> tcpQueue;

    public TcpTrafficInterceptor(BlockingQueue<TcpInfos> tcpQueue) {
        this.tcpQueue = tcpQueue;
    }

    static PcapNetworkInterface CaptureDevice() {
        PcapNetworkInterface device = null;

        try {
            device = new NifSelector().selectNetworkInterface();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return device;

    }

    public void Scan() throws PcapNativeException, NotOpenException {
        PcapNetworkInterface device = CaptureDevice();
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
                .bufferSize(16 * 1024 * 1024)
                .immediateMode(true)
                .build();

        handle.setFilter("tcp", BpfProgram.BpfCompileMode.OPTIMIZE);
        PacketListener listener = new PacketListener() {
            @Override
            public void gotPacket(Packet packet){
                IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
                IpV6Packet ipV6Packet = packet.get(IpV6Packet.class);
                IpPacket ipPacket = null;
                if (ipV4Packet != null) {
                    ipPacket = ipV4Packet;
                } else if (ipV6Packet != null) {
                    ipPacket = ipV6Packet;
                }

                if (ipPacket != null) {
                    TcpPacket tcpPacket = packet.get(TcpPacket.class);
                    if (tcpPacket != null) {
                        TcpInfos tcpinfos = new TcpInfos(ipPacket, tcpPacket);
                        try {
                            tcpQueue.put(tcpinfos);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        };

        try {
            handle.loop(-1, listener);
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