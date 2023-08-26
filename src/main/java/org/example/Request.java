package org.example;


import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Request {
    private String method;
    private String path;
    private List<String> headers;
    private String body;


    public Request(String method, String path, List<String> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
    }

    public Request(String method, String path, List<String> headers) {
        this.method = method;
        this.path = path;
        this.headers = headers;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getPathWithoutQuery() {
        if (path.contains("?")) {
            int indexQuery = path.indexOf("?");
            return path.substring(0, indexQuery);
        } else {
            return path;
        }
    }

    protected List<NameValuePair> getQueryParams() {
        List<NameValuePair> listUrlParameters = URLEncodedUtils.parse(path, StandardCharsets.UTF_8);

        return listUrlParameters;

    }

    protected String getQueryParam(String name) {
        String url = URLDecoder.decode(URLEncodedUtils.format(getQueryParams(), StandardCharsets.UTF_8), StandardCharsets.UTF_8);

        if (url.contains("?")) {
            String[] parts = url.split("\\?");
            String queryParams = parts[1];
            if (queryParams.contains("&")) {
                String[] queryParam = queryParams.split("&");

                for (int i = 0; i < queryParam.length; i++) {

                    if (queryParam[i].substring(0, name.length()).equals(name)) {

                        return queryParam[i];
                    }
                }
                return null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
