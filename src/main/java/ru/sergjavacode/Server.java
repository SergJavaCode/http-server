package ru.sergjavacode;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    static ExecutorService executeIt = Executors.newFixedThreadPool(64);
    private Map<String, Map<String, Handler>> mapAllHandlers = new HashMap<>();
    private Map<String, Handler> mapPOSTHandlers = new HashMap<>();
    private Map<String, Handler> mapGETHandlers = new HashMap<>();

    public Server() {

    }

    public void run(int port) {
        mapAllHandlers.put("GET", mapGETHandlers);
        mapAllHandlers.put("POST", mapPOSTHandlers);
        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                addConnect(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addConnect(Socket socket) {
        executeIt.execute(new ServerClientThread(socket, mapAllHandlers));
    }

    public void addHandler(String typeRequest, String pathRequest, Handler handler) {
        if (typeRequest.equals("GET")) {
            mapGETHandlers.put(pathRequest, handler);
        }
        if (typeRequest.equals("POST")) {
            mapPOSTHandlers.put(pathRequest, handler);
        }
    }
}
