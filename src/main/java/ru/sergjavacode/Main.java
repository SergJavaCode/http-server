package ru.sergjavacode;

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        final var server = new Server();
        // код инициализации сервера (из вашего предыдущего ДЗ)
        // добавление хендлеров (обработчиков)
        server.addHandler("GET", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                // TODO: handlers code
                final var validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
                System.out.println("Получен гет запрос с путем /messages ");
                String resourse = "/" + request.getRequestLine().split(" ")[1].split("/")[request.getRequestLine().split(" ")[1].split("/").length - 1];

                if (!validPaths.contains(resourse)) {
                    responseStream.write((
                            "HTTP/1.1 404 Not Found\r\n" +
                                    "Content-Length: 0\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    responseStream.flush();
                } else {
                    final var filePath = Path.of(".", request.getRequestLine().split(" ")[1]);
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
        server.addHandler("POST", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {


                // TODO: handlers code
            }
        });

        server.run(9999);

    }

}
