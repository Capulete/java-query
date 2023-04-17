package org.example;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int threadPool;
    private final int PORT;
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Handler>> handlers = new ConcurrentHashMap<>();

    public Server(int port, int threadPool) {
        this.PORT = port;
        this.threadPool = threadPool;
    }

    public void addHandler(String method, String path, Handler handler) {
        handlers.putIfAbsent(method, new ConcurrentHashMap<>());
        var methodMap = handlers.get(method);
        methodMap.put(path, handler);
    }

    public void start() {
        try (final var serverSocket = new ServerSocket(PORT)) {

            ExecutorService service = Executors.newFixedThreadPool(threadPool);
            while (true) {
                var socket = serverSocket.accept();
                service.execute(() -> handle(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handle(Socket socket) {
        // read only request line for simplicity
        // must be in form GET /path HTTP/1.1
        try (final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final var out = new BufferedOutputStream(socket.getOutputStream());) {

            final var requestLine = in.readLine();

            if (requestLine == null) {
                System.out.println("Bad request!");
                return;
            }

            Request request = new Request(requestLine);
            request.getQueryParam()
                    .stream()
                    .forEach(System.out::println);

            System.out.println("Значение \"value\" = ");
            request.getQueryParam("value").forEach(System.out::println);

            System.out.println("Значение \"last\" = ");
            request.getQueryParam("last").forEach(System.out::println);

            System.out.println(Thread.currentThread().getName() + " received a request: " + requestLine);

            final var parts = requestLine.split(" ");

            if (parts.length != 3) {
                // just close socket
                return;
            }

            var methodMap = handlers.get(request.getMethod());

            if (methodMap == null) {
                notFound404(out);
                return;
            }

            var handler = methodMap.get(request.getPath());

            if (handler == null) {
                notFound404(out);
                return;
            }

            handler.handle(request, out);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void notFound404(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }
}