package ru.sergjavacode;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerHTTP {
    private static ServerSocket server;
    private static int serverPort;
    private static boolean isServerCreated = false;
    final static List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png",
            "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html",
            "/classic.html", "/events.html", "/events.js");
    final static Map<String, Handler> handlers = new ConcurrentHashMap<>();
    final static ExecutorService executorService = Executors.newFixedThreadPool(64);

    public static ServerSocket getServer(int port) throws IOException {
        if (!isServerCreated) {
            serverPort = port;
            isServerCreated = true;
            server = new ServerSocket(port);
        }
        return server;
    }

    public static void serverAnswer(String[] parts, BufferedOutputStream out) throws IOException {

        final var path = parts[1];
        if (!validPaths.contains(path)) {
            out.write((
                    "HTTP/1.1 404 Not Found\r\n" +
                            "Content-Length: 0\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.flush();
            return;
        }

        final var filePath = Path.of(".", "public", path);
        final var mimeType = Files.probeContentType(filePath);

        // special case for classic
        if (path.equals("/classic.html")) {
            final var template = Files.readString(filePath);
            final var content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString()
            ).getBytes();
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + content.length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.write(content);
            out.flush();
            return;
        }

        final var length = Files.size(filePath);
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        Files.copy(filePath, out);
        out.flush();
    }

    public static void addHandler(String requestType, String pathFile, Handler handler) {
        boolean flag = false;
        for (Map.Entry<String, Handler> entry : handlers.entrySet()) {
            if (entry.getKey().equals(requestType + pathFile)) {
                flag = true;
                break;
            }
        }
        if (!flag) handlers.put(requestType + pathFile, handler);
    }

    public static Handler getHandler(MyRequest request) {
        var parts = request.getStartingLine().split(" ");
        var findKey = parts[0] + "/" + parts[1].split("/")[1];
        for (Map.Entry<String, Handler> entry : handlers.entrySet()) {
            if (entry.getKey().equals(findKey)) {
                return entry.getValue();
            }
        }
        return new Handler() {
            @Override
            public void handle(MyRequest string, BufferedOutputStream responseServer) throws IOException {
                responseServer.write((
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n").getBytes());
                responseServer.flush();
            }
        };
    }

    public static List<String> getPathRequest(MyRequest myRequest) {
        return myRequest.getPathRequest();
    }

    public static void start(ServerSocket serverSocket) {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                executorService.execute(new Thread(() -> {
                    try (final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                         final var out = new BufferedOutputStream(socket.getOutputStream())) {
                        String response = IOUtils.toString(in);
                        String[] responsePart = response.split("\\r\\n\\r\\n");
                        List<String> requestLineAndHeaders = List.of(responsePart[0].split("\\r\\n")) ;
                        final String[] parts;
                        MyRequest request;
                        if ((requestLineAndHeaders.get(0).startsWith("GET") ||
                                requestLineAndHeaders.get(0).startsWith("POST") ||
                                requestLineAndHeaders.get(0).startsWith("PUT") ||
                                requestLineAndHeaders.get(0).startsWith("OPTIONS") ||
                                requestLineAndHeaders.get(0).startsWith("HEAD") ||
                                requestLineAndHeaders.get(0).startsWith("PATCH") ||
                                requestLineAndHeaders.get(0).startsWith("DELETE") ||
                                requestLineAndHeaders.get(0).startsWith("TRACE") ||
                                requestLineAndHeaders.get(0).startsWith("CONNECT")) &&
                                requestLineAndHeaders.get(0).split(" ").length == 3
                        ) {
                            parts = requestLineAndHeaders.get(0).split(" ");
                            request = new MyRequest(requestLineAndHeaders.get(0));
                            if (requestLineAndHeaders.contains("Content-Type: application/x-www-form-urlencoded")
                                    &&requestLineAndHeaders.get(0).startsWith("POST")
                                    &&responsePart.length==2){
                                List<NameValuePair> bodyListNVP= URLEncodedUtils.parse(response.split("\\r\\n\\r\\n")[1], Charset.defaultCharset());
                                request.setBodyListNVP(bodyListNVP);
                            }

                            for (int i = 1; i < requestLineAndHeaders.size(); i++) {
                                request.addHeader(requestLineAndHeaders.get(i));
                            }

                            getPathRequest(request);
                            if (parts.length != 3) {
                                socket.close();
                            }
                            Handler handler = ServerHTTP.getHandler(request);
                            handler.handle(request, out);
                            socket.close();
                        } else {
                            out.write((
                                    "HTTP/1.1 404 Not Found\r\n" +
                                            "Content-Length: 0\r\n" +
                                            "Connection: close\r\n" +
                                            "\r\n"
                            ).getBytes());
                            out.flush();
                            socket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
