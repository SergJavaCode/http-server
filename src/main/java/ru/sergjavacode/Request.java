package ru.sergjavacode;

import java.util.List;

public class Request {
    private final String requestLine;

    public String getRequestLine() {
        return requestLine;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public String getMethod() {
        return method;
    }

    private final List<String> headers;
    private final String body;
    private final String method;

    public Request(String method, String requestLine, List<String> headers, String body) {
        this.method = method;
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    private Request() {
        body = null;
        headers = null;
        requestLine = null;
        method = null;
    }
}
