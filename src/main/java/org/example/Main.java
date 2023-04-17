package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        final int PORT = 8888;
        final int threadPool = 64;

        Server server = new Server(PORT, threadPool);

        // добавление хендлеров (обработчиков)
        server.addHandler("GET", "/messages", (request, out) -> {
            String greeting = "Hello from GET /messages";

            try {
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + "text/plain" + "\r\n" +
                                "Content-Length: " + greeting.length() + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());

                out.write(greeting.getBytes());
                out.flush();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
        server.addHandler("POST", "/messages", (request, out) -> {
            String greeting = "Hello from POST /messages";

            try {
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + "text/plain" + "\r\n" +
                                "Content-Length: " + greeting.length() + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());

                out.write(greeting.getBytes());
                out.flush();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });

        server.start();
    }
}