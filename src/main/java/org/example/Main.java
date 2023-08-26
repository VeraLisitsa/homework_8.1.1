package org.example;

import java.io.BufferedOutputStream;
import java.io.IOException;


public class Main {
    public static void main(String[] args) {
        Server server = new Server(9999);

        server.addHandler("GET", "/index.html", new Handler() {
            public void handle(Request request, BufferedOutputStream out) {
                try {
                    Response response = new Response(out, request.getPathWithoutQuery());
                    response.sendResponse();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        server.addHandler("GET", "/events.html", new Handler() {
            public void handle(Request request, BufferedOutputStream out) {
                try {
                    Response response = new Response(out, request.getPathWithoutQuery());
                    response.sendResponse();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        server.addHandler("GET", "/forms.html", new Handler() {
            public void handle(Request request, BufferedOutputStream out) {
                try {
                    Response response = new Response(out, request.getPathWithoutQuery());
                    response.sendResponse();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        server.addHandler("GET", "/classic.html", new Handler() {
            public void handle(Request request, BufferedOutputStream out) {
                try {
                    Response response = new Response(out, request.getPathWithoutQuery());
                    response.sendResponseClassic();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        server.start();
    }
}