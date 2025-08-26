package com.seguranca.rede.scanner.Services;

import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.util.NifSelector;
import org.pcap4j.core.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class Scanner {

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

    public void Scannear(int maxPackets) throws PcapNativeException, NotOpenException {
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
            public void gotPacket(Packet packet) {

                    // System.out.println(handle.getNextPacket());

                // System.out.println(packet);
                TcpPacket tcpPacket = packet.get(TcpPacket.class);

                if (tcpPacket != null && tcpPacket.getPayload() != null) {
                    byte[] payload = tcpPacket.getPayload().getRawData();
                    String data = new String(payload, StandardCharsets.UTF_8);

                    if (data.contains("HTTP")) {
                        System.out.println("=== HTTP detectado ===");
                        System.out.println(data);

                        if (data.contains("{") && data.contains("}")) {
                            System.out.println("=== JSON detectado ===");
                            System.out.println(data.substring(data.indexOf("{"), data.indexOf("}") + 1));
                        }
                    }
                }
            }
        };

        try {
            handle.loop(maxPackets, listener);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        handle.close();
    }
}