package com.seguranca.rede.scanner.Services.TCP;

import com.seguranca.rede.scanner.PacketInfo.TCPinfos;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.NifSelector;
import org.pcap4j.core.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public List<TCPinfos> Scannear(int maxPackets) throws PcapNativeException, NotOpenException {

        List<TCPinfos> listaDePacotes = new ArrayList<>();
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
                TCPinfos tcpinfos = new TCPinfos(packet);
                listaDePacotes.add(tcpinfos);
            }
        };

        try {
            handle.loop(maxPackets, listener);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        handle.close();

        return listaDePacotes;
    }
}