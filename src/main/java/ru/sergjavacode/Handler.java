package ru.sergjavacode;


import java.io.BufferedOutputStream;
import java.io.IOException;

@FunctionalInterface
public interface Handler {
    public abstract void handle(Request request, BufferedOutputStream responseStream) throws IOException;
}
