package ir.mkay.simplehttpproxy;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class Proxier {

    private final String targetUrl;
    private final HttpURLConnection connection;
    private final HttpExchange exchange;
    private boolean responseHeadersSent = false;

    public Proxier(String targetUrl, HttpExchange exchange) throws IOException {
        this.targetUrl = targetUrl;
        URL url = new URL(targetUrl);
        this.connection = (HttpURLConnection) url.openConnection();
        this.connection.setInstanceFollowRedirects(false);
        this.exchange = exchange;
    }

    public void proxy() throws Throwable {
        copyMethod();
        copyRequestHeaders();
        copyRequestContent();
        try {
            setTimeouts();
            copyResponseHeaders();
            sendResponseHeaders();
            copyResponseContent();
        } catch (Throwable e) {
            if (!responseHeadersSent) sendProxyError(e);
            else throw e;
        }
    }

    private void copyRequestHeaders() {
        copyHeaders(exchange.getRequestHeaders(), connection::setRequestProperty);
    }

    private void copyMethod() throws ProtocolException {
        connection.setRequestMethod(exchange.getRequestMethod());
    }

    private void copyRequestContent() throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            connection.setDoOutput(true);
            try (InputStream in = exchange.getRequestBody()) {
                try (OutputStream out = connection.getOutputStream()) {
                    copyInToOut(in, out);
                }
            }
        }
    }

    private void setTimeouts() {
        connection.setConnectTimeout(Configurations.getProxyConnectTimeout());
        connection.setReadTimeout(Configurations.getProxyReadTimeout());
    }

    private void copyResponseHeaders() {
        copyHeaders(connection.getHeaderFields(), exchange.getResponseHeaders()::add);
    }

    private void sendResponseHeaders() throws IOException {
        int contentLength = connection.getContentLength();
        /*
            When getContentLength() returns -1, it means there is no 'Content-Length' header present;
            But in sendResponseHeaders method, -1 (as contentLength) means no response content should be sent;
            Therefore when we get -1 from getContentLength(), we set contentLength to 0 (which means arbitrary length of content
            in sendResponseHeaders method).
         */
        if (contentLength == -1) contentLength = 0;

        exchange.sendResponseHeaders(connection.getResponseCode(), contentLength);
        this.responseHeadersSent = true;

    }

    private void copyResponseContent() throws IOException {
        InputStream in;
        if (connection.getResponseCode() < 400) {
            in = connection.getInputStream();
        } else {
            in = connection.getErrorStream();
        }

        try (OutputStream out = exchange.getResponseBody()) {
            copyInToOut(in, out);
        } finally {
            if (in != null) in.close();
        }
    }


    private void sendProxyError(Throwable e) throws IOException {
        StringWriter responseContent = new StringWriter();
        responseContent.append("Proxy Error");
        if (Configurations.shouldAddProxyErrorDetailsToResponse()) {
            responseContent.append("; ");
            e.printStackTrace(new PrintWriter(responseContent));
        } else {
            responseContent.append(".");
        }
        byte[] responseContentBytes = responseContent.toString().getBytes();
        exchange.sendResponseHeaders(500, responseContentBytes.length);
        exchange.getResponseBody().write(responseContentBytes);
    }

    private void copyInToOut(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[Configurations.getBufferSize()];
        int len;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
    }

    private void copyHeaders(Map<String, List<String>> in, BiConsumer<String, String> out) {
        in.forEach((name, values) -> {
            if (name != null && !name.isEmpty()) {
                values.forEach(v -> out.accept(name, v));
            }
        });
    }

}
