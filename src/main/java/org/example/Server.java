package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    int port;
    final static List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");

    public Server(int port) {
        this.port = port;

    }

    public void start() {
        ExecutorService executorService = Executors.newFixedThreadPool(64);

        try (final var serverSocket = new ServerSocket(port)) {

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    executorService.execute(new Thread(new RequestThread(clientSocket)));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
}
