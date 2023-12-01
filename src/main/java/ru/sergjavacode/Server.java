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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    static ExecutorService executeIt = Executors.newFixedThreadPool(64);

    public void run() {
        try (final var serverSocket = new ServerSocket(9999)) {
            while (true) {
                final var socket = serverSocket.accept();
                addConnect(socket);
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addConnect(Socket socket) {
        executeIt.execute(new MonoThreadClientHandler(socket));
    }
}
