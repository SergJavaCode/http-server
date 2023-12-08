package ru.sergjavacode;


import java.io.BufferedOutputStream;
import java.io.IOException;

@FunctionalInterface
public interface Handler {
    void handle(MyRequest request, BufferedOutputStream responseServer) throws IOException;
}
