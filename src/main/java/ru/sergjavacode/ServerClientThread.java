package ru.sergjavacode;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

public class ServerClientThread implements Runnable {
    private Socket socket;
    private Map<String, Map<String, Handler>> mapAllHandlers;

    public ServerClientThread(Socket client, Map<String, Map<String, Handler>> mapAllHandlers) {
        this.socket = client;
        this.mapAllHandlers = mapAllHandlers;
    }

    @Override
    public void run() {
        String requestLine = new String();
        String method = new String();
        String path = new String();
        List<String> headers = new ArrayList<>();
        StringBuilder body = new StringBuilder();
        boolean bodyStart = false;
        boolean responseCanceled = false;
        try (
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream());

        ) {
            String line = in.readLine();
            Optional<String> optLine = Optional.ofNullable(line);
            if ((optLine.get().startsWith("GET") ||
                    optLine.get().startsWith("POST") ||
                    optLine.get().startsWith("PUT") ||
                    optLine.get().startsWith("OPTIONS") ||
                    optLine.get().startsWith("HEAD") ||
                    optLine.get().startsWith("PATCH") ||
                    optLine.get().startsWith("DELETE") ||
                    optLine.get().startsWith("TRACE") ||
                    optLine.get().startsWith("CONNECT")) &&
                    optLine.get().split(" ").length == 3
            ) {
                requestLine = optLine.get();
                method = optLine.get().split(" ")[0];
                path = optLine.get().split(" ")[1];

//                        final var mimeType = Files.probeContentType(Path.of("."+path));
//                        final var length = Files.size(Path.of("."+path));
//                        out.write((
//                                "HTTP/1.1 200 OK\r\n" +
//                                        "Content-Type: " + mimeType + "\r\n" +
//                                        "Content-Length: " + length + "\r\n" +
//                                        "Connection: close\r\n" +
//                                        "\r\n"
//                        ).getBytes());
//                        Files.copy(Path.of("."+path), out);
//                        out.flush();

                bodyStart = false;
                responseCanceled = false;

            }
            in.lines().forEach(s -> {
                headers.add(s);
            });

            Request request = new Request(method, requestLine, headers, body.toString());
            if (path.contains("/messages")) {
                mapAllHandlers.get(method).get("/messages").handle(request, out);
            }
            responseCanceled = true;
            socket.close();
//            while (true) {
//                line = in.readLine();
//                if (line==null){
//
//                    break;
//                }
//                // read only request line for simplicity
//                // must be in form GET /path HTTP/1.1
//
//                // if (optLine.isEmpty()) {break;}
//                if (line.equals("") || bodyStart) {
//                    body.append(optLine.get());
//                    bodyStart = true;
//
//                } else {
//                    headers.add(optLine.get());
//                }
//
//
//            }


        } catch (IOException e) {
            throw new RuntimeException(e);

        }

    }


}
