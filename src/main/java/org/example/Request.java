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
    private String queryString;


    public Request(String method, String path, List<String> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
        this.queryString = getQueryParams();
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
    public String getQueryString(){
        return queryString;
    }


    protected String getPathWithoutQuery() {
        if (path.contains("?")) {
            int indexQuery = path.indexOf("?");
            return path.substring(0, indexQuery);
        } else {
            return path;
        }
    }

    private String getQueryParams() {
        List<NameValuePair> listUrlParameters = URLEncodedUtils.parse(path, StandardCharsets.UTF_8);
        String url = URLDecoder.decode(URLEncodedUtils.format(listUrlParameters, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        if (url.contains("?")) {
            String[] parts = url.split("\\?");
            String query = parts[1];

            return query;
        } else {
            return null;
        }
    }

    protected String getQueryParam(String name) {

        if (queryString!=null) {
            if (queryString.contains("&")) {
                String[] queryParam = queryString.split("&");

                for (int i = 0; i < queryParam.length; i++) {

                    if (queryParam[i].length() > name.length() && queryParam[i].substring(0, name.length()).equals(name)) {
                        return queryParam[i].substring(name.length() + 1);
                    }
                }
            } else {
                if (queryString.length() > name.length() && queryString.substring(0, name.length()).equals(name)) {
                    return queryString.substring(name.length() + 1);
                }
            }
        }
        return null;
    }

}
