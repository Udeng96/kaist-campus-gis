package com.eseict.ibsmodule.tcp;

import com.eseict.ibsmodule.service.packet.PacketHandler;
import com.eseict.ibsmodule.service.stream.NMuxStreamService;
import com.eseict.ibsmodule.service.stream.TcpStreamService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

@Component
@RequiredArgsConstructor
public class TcpEventClient implements Runnable {

    @Value("${nmux.host}")
    String host;
    @Value("${nmux.port}")
    int port;

    private final PacketHandler packetHandler;

    private volatile boolean running = true;

    private static final Logger log = LoggerFactory.getLogger(TcpEventClient.class);

    private static final long INITIAL_RETRY_MS = 3_000;
    private static final long MAX_RETRY_MS = 60_000;

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        long retryDelay = INITIAL_RETRY_MS;

        while (running) {
            try (Socket socket = new Socket(host, port)) {
                socket.setSoTimeout(60_000);
                socket.setKeepAlive(true);

                InputStream in = socket.getInputStream();
                log.info("[NMUX CONN] SUCCESS - {}:{}", host, port);

                // 연결 성공 시 재연결 대기시간 리셋
                retryDelay = INITIAL_RETRY_MS;

                TcpStreamService tcpStreamService = new NMuxStreamService(packetHandler);

                byte[] buffer = new byte[4096];
                int read;

                while (running && (read = in.read(buffer)) != -1) {
                    tcpStreamService.onBytes(buffer, read);
                }

                log.warn("[NMUX READ] EOF - SERVER CLOSED CONNECTION");
            } catch (SocketTimeoutException e) {
                log.warn("[NMUX CONN] READ TIMEOUT (60s) - RECONNECT");
            } catch (IOException e) {
                if (running) {
                    log.error("[NMUX IO] IO ERROR - RECONNECT IN {}s", retryDelay / 1000, e);
                }
            }

            if (!running) break;

            try {
                Thread.sleep(retryDelay);
                // Exponential backoff: 3s → 6s → 12s → 24s → 48s → 60s(max)
                retryDelay = Math.min(retryDelay * 2, MAX_RETRY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        log.info("[NMUX CONN] TcpEventClient stopped");
    }
}
