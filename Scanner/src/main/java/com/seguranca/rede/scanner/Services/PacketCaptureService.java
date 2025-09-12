package com.seguranca.rede.scanner.Services;

import com.seguranca.rede.scanner.PacketInfo.HttpInfos;
import com.seguranca.rede.scanner.PacketInfo.TcpInfos;
import com.seguranca.rede.scanner.Services.TCP.TcpTrafficInterceptor;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
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

    private String normalizeAddress(String ipv4Address) {
        try {
            // Cria um objeto InetAddress a partir do endereço IPv4.
            InetAddress address = InetAddress.getByName(ipv4Address);

            // Obtém os bytes do endereço IPv4.
            byte[] ipv4Bytes = address.getAddress();

            // Cria um array de bytes de 16 posições para o endereço IPv6.
            byte[] ipv6Bytes = new byte[16];

            // Os primeiros 10 bytes são zero para a representação "IPv4-mapped".
            for (int i = 0; i < 10; i++) {
                ipv6Bytes[i] = 0;
            }

            // O 11º e 12º bytes são -1 (equivalente a 0xff em hexadecimal).
            // Isso indica a representação IPv4-mapped.
            ipv6Bytes[10] = (byte) 0xff;
            ipv6Bytes[11] = (byte) 0xff;

            // Copia os 4 bytes do endereço IPv4 para o final do array IPv6.
            System.arraycopy(ipv4Bytes, 0, ipv6Bytes, 12, 4);

            // Converte o array de bytes IPv6 para a representação de string.
            InetAddress ipv6Address = InetAddress.getByAddress(ipv6Bytes);

            // Retorna a representação de string formatada.
            // A classe InetAddress já cuida da formatação correta.
            return ipv6Address.getHostAddress();

        } catch (UnknownHostException e) {
            System.err.println("Erro: Endereço IPv4 inválido.");
            return null;
        }
    }

    Map<String, HttpInfos> connections = new ConcurrentHashMap<>();

    public void startConnectPackets(int seconds) {
        TCPCapture.submit(() -> {
            try {
                new TcpTrafficInterceptor(tcpQueue).Scannear(seconds);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

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
                    System.out.println("Esperando requisições");
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
