package org.example;


import org.apache.http.NameValuePair;
import java.util.List;
import java.util.stream.Collectors;

public class Request {
    private String method;
    private String path;
    private List<String> headers;
    private String body;
    private List<NameValuePair> queryParams;


    public Request(String method, String path, List<String> headers, String body,List<NameValuePair> queryParams) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
        this.queryParams = queryParams;

    }

    public Request(String method, String path, List<String> headers, List<NameValuePair> queryParams) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.queryParams = queryParams;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }


    protected String getPathWithoutQuery() {
        if (path.contains("?")) {
            int indexQuery = path.indexOf("?");
            return path.substring(0, indexQuery);
        } else {
            return path;
        }
    }

    protected List<NameValuePair> getQueryParams() {
      return queryParams;
    }

    protected List<NameValuePair> getQueryParam(String name) {
        if (queryParams!=null) {
           return queryParams.stream()
                   .filter(o -> o.getName().equals(name))
                    .collect(Collectors.toList());
        }
        return null;
    }


}
