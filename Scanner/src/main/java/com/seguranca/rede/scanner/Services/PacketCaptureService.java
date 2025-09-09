package com.seguranca.rede.scanner.Services;

import com.seguranca.rede.scanner.PacketInfo.HttpInfos;
import com.seguranca.rede.scanner.PacketInfo.TcpInfos;
import com.seguranca.rede.scanner.Services.TCP.TcpTrafficInterceptor;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class PacketCaptureService {
    private ExecutorService TCPCapture = Executors.newSingleThreadExecutor();
    private ExecutorService connectTCP = Executors.newSingleThreadExecutor();
    private ExecutorService connectHTTP = Executors.newSingleThreadExecutor();

    private BlockingQueue<TcpInfos> tcpQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<HttpInfos> httpQueue;

    public PacketCaptureService(BlockingQueue<HttpInfos> httpQueue) {
        this.httpQueue = httpQueue;
    }

    private String normalizeAddress(String address) {
        try {
            InetAddress inet = InetAddress.getByName(address);

            // 1. Caso seja IPv4 normal
            if (inet instanceof java.net.Inet4Address) {
                return inet.getHostAddress();
            }

            // 2. Caso seja IPv6 mapeado para IPv4 (::ffff:...)
            if (inet instanceof java.net.Inet6Address) {
                byte[] bytes = inet.getAddress();
                if (bytes.length == 16) {
                    boolean isIpv4Mapped = true;
                    for (int i = 0; i < 10; i++) {
                        if (bytes[i] != 0) {
                            isIpv4Mapped = false;
                            break;
                        }
                    }
                    if (bytes[10] == (byte) 0xff && bytes[11] == (byte) 0xff && isIpv4Mapped) {
                        return String.format("%d.%d.%d.%d",
                                bytes[12] & 0xff, bytes[13] & 0xff,
                                bytes[14] & 0xff, bytes[15] & 0xff);
                    }
                }

                // 3. Se for loopback IPv6 (::1), converte para 127.0.0.1
                if (inet.isLoopbackAddress()) {
                    return "127.0.0.1";
                }

                // 4. Se chegou aqui, é IPv6 puro → ou mantemos, ou forçamos fallback
                //return inet.getHostAddress(); // mantém IPv6 real
                return "0.0.0.0"; // <-- força sempre IPv4 mesmo que inventado
            }

            return inet.getHostAddress();

        } catch (UnknownHostException e) {
            return address; // fallback se não resolver
        }
    }



    private List<TcpInfos> tcpList = new ArrayList<>();
    Map<String, HttpInfos> connections = new ConcurrentHashMap<>();

    public void startCaptureTCP(int seconds) {
        TCPCapture.submit(() -> {
            try {
                tcpList = new TcpTrafficInterceptor().Scannear(seconds);
                for (TcpInfos tcpInfos : tcpList) {
                    tcpQueue.put(tcpInfos);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void startConnectPackets() {
        connectTCP.submit(() -> {
            try {
                while (true) {
                    TcpInfos tcp = tcpQueue.take();
                    String key = tcp.getLocalAddress() + ":"
                                + tcp.getLocalPort() + "-"
                                + tcp.getRemoteAddress() + ":"
                                + tcp.getRemotePort();
                    System.out.println("tcp-key" + key);
                    connections.computeIfAbsent(key, k->new HttpInfos()).addTcpPacket(tcp);
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        connectHTTP.submit(() -> {
            try {
                while (true) {
                    HttpInfos http = httpQueue.take();
                    String addLocal = normalizeAddress(http.getLocalAddress());
                    String addRemote = normalizeAddress(http.getRemoteAddress());
                    String key = addLocal + ":" +
                            http.getLocalPort() + "-" +
                            addRemote + ":" +
                            http.getRemotePort();
                    System.out.println("http-key" + key);
                    connections.putIfAbsent(key, http);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


}
