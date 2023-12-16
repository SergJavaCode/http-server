package ru.sergjavacode;

import org.apache.http.NameValuePair;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.http.client.utils.URLEncodedUtils;

public class MyRequest {


    private List<NameValuePair> queryParams = new ArrayList<>();
    private String startingLine;
    private List<String> headers = new ArrayList<>(); //добавить заполнение в сервере
    private List<NameValuePair> bodyListNVP;
    public MyRequest(String requestLine) {

        this.startingLine = requestLine;
        queryParams = URLEncodedUtils.parse(requestLine.split(" ")[1].split("\\?")[1], Charset.defaultCharset());
    }

    public Optional<List<NameValuePair>> getPostParam() {
        return Optional.ofNullable(bodyListNVP);
    }
    public  Optional<List<NameValuePair>> etPostParam(String postParam){
        return Optional.of(queryParams.stream().filter(s -> s.getName().equals(postParam)).collect(Collectors.toList()));
    }
    public void setBodyListNVP(List<NameValuePair> bodyListNVP) {
        this.bodyListNVP = bodyListNVP;
    }

    public List<String> getPathRequest() {
        List<String> pathSegments = URLEncodedUtils.parsePathSegments(this.getStartingLine().split(" ")[1].split("\\?")[0]);
        return pathSegments;
    }

    public Optional<List<NameValuePair>> getQueryParam() {
        return Optional.ofNullable(queryParams);
    }
    public Optional<NameValuePair>  getQueryParam(String queryParam) {
        return queryParams.stream().filter(s->s.getName().equals(queryParam)).findFirst();
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
