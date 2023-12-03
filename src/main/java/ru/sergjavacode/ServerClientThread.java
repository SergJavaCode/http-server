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
            while (!socket.isClosed()) {
                // read only request line for simplicity
                // must be in form GET /path HTTP/1.1
                final String line = in.readLine();
                final Optional<String> optLine = Optional.ofNullable(line);
                if (optLine.isPresent()) {
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
                        path = "/" + optLine.get().split(" ")[1].split("/")[1];
                        bodyStart = false;
                        responseCanceled = false;

                    } else if (optLine.get().equals("") || bodyStart) {
                        body.append(optLine.get());
                        bodyStart = true;
                    } else {
                        headers.add(optLine.get());
                    }

                } else {
                    if (!responseCanceled) {
                        Request request = new Request(method, requestLine, headers, body.toString());
                        out.write("xxxxxxxx".getBytes());
                        out.flush();
                        mapAllHandlers.get(method).get(path).handle(request, out);
                        responseCanceled = true;
                    }


                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
