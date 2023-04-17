package org.example;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

public class Request {
    private final String requestLine;
    private final String method;
    private final String path;
    private final String query;
    private final List<NameValuePair> queryParam;
    public Request(String requestLine) {
        this.requestLine = requestLine;

        String[] requestLineParts = requestLine.split(" ");

        this.method = requestLineParts[0];

        var uri = URI.create(requestLineParts[1]);

        this.path = uri.getPath();

        this.query = uri.getQuery();

        this.queryParam = URLEncodedUtils.parse(uri, "UTF-8");
    }

    public String getRequestLine() {
        return requestLine;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQuery() {
        return query;
    }

    public List<NameValuePair> getQueryParam() {
        return queryParam;
    }

    public List<NameValuePair> getQueryParam(String name) {
        return queryParam.stream()
                .filter(nameValuePair -> nameValuePair.getName().equals(name))
                .collect(Collectors.toList());
    }
}