package ru.sergjavacode;

import org.apache.http.NameValuePair;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.utils.URLEncodedUtils;

public class MyRequest {


    private List<NameValuePair> queryParam = new ArrayList<>();
    private String startingLine;
    private List<String> headers = new ArrayList<>();

    public MyRequest(String requestLine) {

        this.startingLine = requestLine;
        queryParam = URLEncodedUtils.parse(requestLine.split(" ")[1].split("\\?")[1], Charset.defaultCharset());
    }

    public List<String> getPathRequest() {
        List<String> pathSegments = URLEncodedUtils.parsePathSegments(this.getStartingLine().split(" ")[1].split("\\?")[0]);
        return pathSegments;
    }

    public List<NameValuePair> getQueryParam() {
        return queryParam;
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
