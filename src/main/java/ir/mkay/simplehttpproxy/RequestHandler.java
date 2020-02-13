package ir.mkay.simplehttpproxy;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements HttpHandler {

    private static final Logger log = Logger.getLogger(RequestHandler.class.getSimpleName());

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Proxier proxier = new Proxier(Configurations.getProxyUrl() + exchange.getRequestURI(), exchange);
            proxier.proxy();
        } catch (Exception e) {
            exchange.getResponseHeaders().add("proxy-error", e.getMessage());
            log.log(Level.WARNING, "At '" + exchange.getRequestURI() + "'", e);
            throw e;
        }
    }
}
