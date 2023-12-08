package ru.sergjavacode;

import java.util.ArrayList;
import java.util.List;

public class MyRequest {
    private String startingLine;
    private List<String> headers = new ArrayList<>();

    public MyRequest(String requestLine) {
        this.startingLine = requestLine;
    }

    public void addHeader(String header) {
        headers.add(header);
    }

    public void showHeaders() {
        for (String str : headers) {
            System.out.println(str);
        }
    }

    public String getStartingLine() {
        return startingLine;
    }
}
