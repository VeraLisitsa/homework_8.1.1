package org.example;


import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RequestThread implements Runnable {
    private Socket clientSocket;
    private static final String GET = "GET";
    private static final String POST = "POST";
    private final List<String> allowedMethods = List.of(GET, POST);
    private Request request;

    protected RequestThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (
                final var in = new BufferedInputStream(clientSocket.getInputStream());
                final var out = new BufferedOutputStream(clientSocket.getOutputStream());
        ) {
            final var limit = 4096;

            in.mark(limit);
            final var buffer = new byte[limit];
            final var read = in.read(buffer);

            final var requestLineDelimiter = new byte[]{'\r', '\n'};
            final var requestLineEnd = indexOf(buffer, requestLineDelimiter, 0, read);

            if (requestLineEnd == -1) {
                badRequest(out);
                return;
            }

            final var requestLine = new String(Arrays.copyOf(buffer, requestLineEnd)).split(" ");

            if (requestLine.length != 3) {
                badRequest(out);
                return;
            }

            final var method = requestLine[0];
            if (!allowedMethods.contains(method)) {
                badRequest(out);
                return;
            }

            final var path = requestLine[1];
            String pathWithOutQuery;
            if (path.contains("?")) {
                int indexQuery = path.indexOf("?");
                pathWithOutQuery = path.substring(0, indexQuery);
            } else {
                pathWithOutQuery = path;
            }

            if (!Server.getValidPaths().contains(pathWithOutQuery)) {
                pathInRequestNoValid(out);
                return;
            }
            if (!path.startsWith("/")) {
                badRequest(out);
                return;
            }


            List<NameValuePair> queryParams = null;

            if (path.contains("?")) {
                String[] parts = path.split("\\?");
                String query = parts[1];
                queryParams = URLEncodedUtils.parse(query, StandardCharsets.UTF_8);
            }

            final var headersDelimiter = new byte[]{'\r', '\n', '\r', '\n'};
            final var headersStart = requestLineEnd + requestLineDelimiter.length;
            final var headersEnd = indexOf(buffer, headersDelimiter, headersStart, read);

            if (headersEnd == -1) {
                badRequest(out);
                return;
            }

            in.reset();

            in.skip(headersStart);

            final var headersBytes = in.readNBytes(headersEnd - headersStart);
            final var headers = Arrays.asList(new String(headersBytes).split("\r\n"));

            if (!method.equals(GET)) {
                in.skip(headersDelimiter.length);

                final var contentLength = extractHeader(headers, "Content-Length");
                if (contentLength.isPresent()) {
                    final var length = Integer.parseInt(contentLength.get());
                    final var bodyBytes = in.readNBytes(length);

                    final var body = new String(bodyBytes);
                    request = new Request(method, path, headers, body, queryParams);
                }
            } else {
                request = new Request(method, path, headers, queryParams);
            }

            System.out.println(request.getQueryParams());
            System.out.println(request.getQueryParam("login"));



            if (request.getMethod().equals("GET")) {
                if (Server.getGetHandlers().containsKey(request.getPathWithoutQuery())) {
                    Server.getGetHandlers().get(request.getPathWithoutQuery()).handle(request, out);
                } else {
                    defaultCase(out);
                }
            } else {
                defaultCase(out);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException ignore) {

                }
            }
        }
    }


    protected void pathInRequestNoValid(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
    }

    protected void defaultCase(BufferedOutputStream out) throws IOException {
        final var content = "<html><body><h1>Hello, world!</h1></body></html>";
        final var mimeType = "text/html";
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + content.length() + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.write((content).getBytes());
        out.flush();
    }

    private int indexOf(byte[] array, byte[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    private void badRequest(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

    private static Optional<String> extractHeader(List<String> headers, String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }
}
