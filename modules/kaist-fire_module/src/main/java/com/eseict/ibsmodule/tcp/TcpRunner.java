package com.eseict.ibsmodule.tcp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class TcpRunner implements CommandLineRunner {
    private final TcpEventClient tcpEventClient;
    private Thread clientThread;

    public TcpRunner(TcpEventClient tcpEventClient) {
        this.tcpEventClient = tcpEventClient;
    }

    @Override
    public void run(String... args) {
        clientThread = new Thread(tcpEventClient, "TcpEventClient-Thread");
        clientThread.start();
    }

    @PreDestroy
    public void shutdown() {
        tcpEventClient.stop();
        if (clientThread != null) {
            clientThread.interrupt();
        }
    }
}
