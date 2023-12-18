package ru.sergjavacode;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MyMain {
    public static void main(String[] args) {
        ServerHTTP.addHandler("GET", "/messages", new Handler() {
            @Override
            public void handle(MyRequest request, BufferedOutputStream responseStream) throws IOException {
                final var validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
                System.out.println("Получен гет запрос с путем /messages ");
                String resourse = "/" + request.getStartingLine().split(" ")[1].split("/")[request.getStartingLine().split(" ")[1].split("/").length - 1];

                if (!validPaths.contains(resourse)) {
                    responseStream.write((
                            "HTTP/1.1 404 Not Found\r\n" +
                                    "Content-Length: 0\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    responseStream.flush();
                } else {
                    final var filePath = Path.of(".", request.getStartingLine().split(" ")[1]);
                    final var mimeType = Files.probeContentType(filePath);
                    final var length = Files.size(filePath);
                    responseStream.write((
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: " + mimeType + "\r\n" +
                                    "Content-Length: " + length + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    Files.copy(filePath, responseStream);
                    responseStream.flush();
                }

            }
        });
        ServerSocket serverSocket = null;
        try {
            serverSocket = ServerHTTP.getServer(9999);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ServerHTTP.start(serverSocket);
    }
}


