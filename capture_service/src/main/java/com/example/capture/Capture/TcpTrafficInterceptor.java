package com.example.capture.Capture;

import com.example.common.PacketInfo.TcpInfos;
import org.pcap4j.packet.*;
import org.pcap4j.core.*;

import java.net.Inet4Address;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class TcpTrafficInterceptor {

    private final BlockingQueue<TcpInfos> tcpQueue;

    public TcpTrafficInterceptor(BlockingQueue<TcpInfos> tcpQueue) {
        this.tcpQueue = tcpQueue;
    }

    public static PcapNetworkInterface CaptureDevice() throws PcapNativeException {
        for (PcapNetworkInterface dev : Pcaps.findAllDevs()) {
            for (PcapAddress addr : dev.getAddresses()) {
                if (addr.getAddress() instanceof Inet4Address) {
                    String ip = addr.getAddress().getHostAddress();
                    if (ip.startsWith("172.18.0")) {        // put here the ip address that you want to watch
                        return dev;
                    }
                }
            }
        }
        return null;
    }

    public void Scan() throws PcapNativeException, NotOpenException {
        PcapNetworkInterface device = CaptureDevice();
        System.out.println("Device chosen : " + device);

        if (device == null) {
            System.out.println("There is no device");
            throw new IllegalStateException("No device found");
        }

        int snapshotlenght = 65536;
        int readTimeout = 1;
        // handler configuration - tried to do similar to Wireshark
        PcapHandle handle = new PcapHandle.Builder(device.getName())
                .snaplen(snapshotlenght)
                .promiscuousMode(PcapNetworkInterface.PromiscuousMode.PROMISCUOUS)
                .timeoutMillis(readTimeout)
                .bufferSize(16 * 1024 * 1024)
                .immediateMode(true)
                .build();

        handle.setFilter("tcp", BpfProgram.BpfCompileMode.OPTIMIZE);
        PacketListener listener = packet -> {
            IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
            IpV6Packet ipV6Packet = packet.get(IpV6Packet.class);
            IpPacket ipPacket = null;
            if (ipV4Packet != null) {       // risk if the packet found is an ipV6 packet
                ipPacket = ipV4Packet;
            } else if (ipV6Packet != null) {
                ipPacket = ipV6Packet;
            }

            if (ipPacket != null) {
                TcpPacket tcpPacket = packet.get(TcpPacket.class);
                if (tcpPacket != null) {
                    TcpInfos tcpinfos = new TcpInfos(ipPacket, tcpPacket); // create a class if an ip can be associated to this packet
                    try {
                        tcpQueue.put(tcpinfos); // puts int the queue
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };

        try {
            handle.loop(-1, listener); // listen until quit the program
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