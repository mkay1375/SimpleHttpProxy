package ir.mkay.simplehttpproxy;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

public class RequestHandler implements HttpHandler {

    private static final Logger log = Logger.getLogger(RequestHandler.class.getSimpleName());

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String from = exchange.getRequestURI().toString();
        String target = Configurations.getProxyUrl() + exchange.getRequestURI();
        try {
            Proxier proxier = new Proxier(target, exchange);
            proxier.proxy();
        } catch (Throwable e) {
            log.log(Level.WARNING, "At '" + exchange.getRequestURI() + "'", e);
            throw new IOException(format("Error while proxying %s to %s", from, target), e);
        }
    }
}
