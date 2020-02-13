package ir.mkay.simplehttpproxy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Configuration {

    private static String proxyUrl;
    private static int bufferSize;
    private static int port;
    private static int threadPoolSize;

    public static void load(String path) throws IOException {
        Properties configs = new Properties();
        configs.load(Files.newInputStream(Paths.get(path)));
        proxyUrl = configs.getProperty("proxyUrl");
        bufferSize = Integer.parseInt(configs.getProperty("bufferSize", "1024"));
        port = Integer.parseInt(configs.getProperty("port", "0"));
        threadPoolSize = Integer.parseInt(configs.getProperty("threadPoolSize", "200"));
    }

    public static String getProxyUrl() {
        return proxyUrl;
    }

    public static int getBufferSize() {
        return bufferSize;
    }

    public static int getPort() {
        return port;
    }

    public static int getThreadPoolSize() {
        return threadPoolSize;
    }

}
