package ir.mkay.simplehttpproxy;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    public Proxier(String targetUrl, HttpExchange exchange) throws IOException {
        this.targetUrl = targetUrl;
        URL url = new URL(targetUrl);
        this.connection = (HttpURLConnection) url.openConnection();
        this.exchange = exchange;
    }

    public void proxy() throws IOException {
        copyRequestHeaders();
        copyMethod();
        copyRequestContent();
        copyResponseHeaders();
        copyStatusAndLength();
        copyResponseContent();
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

    private void copyResponseHeaders() {
        copyHeaders(connection.getHeaderFields(), exchange.getResponseHeaders()::add);
    }

    private void copyStatusAndLength() throws IOException {
        exchange.sendResponseHeaders(connection.getResponseCode(), connection.getContentLength());
    }

    private void copyResponseContent() throws IOException {
        try (InputStream in = connection.getInputStream()) {
            try (OutputStream out = exchange.getResponseBody()) {
                copyInToOut(in, out);
            }
        }
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
