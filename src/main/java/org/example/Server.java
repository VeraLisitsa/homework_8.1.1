package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    private int port;
    private static Map<String, Handler> getHandlers = new HashMap<>();
    private static Map<String, Handler> postHandlers = new HashMap<>();
    private final static List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");

    public Server(int port) {
        this.port = port;

    }

    protected static Map<String, Handler> getGetHandlers() {
        return getHandlers;
    }

    protected static Map<String, Handler> getPostHandlers() {
        return postHandlers;
    }

    protected static List<String> getValidPaths() {
        return validPaths;
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

    protected void addHandler(String method, String path, Handler handler) {
        if (method.equals("GET")) {
            getHandlers.put(path, handler);
        } else {
            postHandlers.put(path, handler);
        }
    }
}
