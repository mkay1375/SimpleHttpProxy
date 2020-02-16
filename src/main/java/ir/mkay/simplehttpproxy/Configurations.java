package ir.mkay.simplehttpproxy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class Configurations {

    private static String proxyUrl;
    private static int bufferSize;
    private static int port;
    private static int threadPoolSize;
    private static int proxyConnectTimeout;
    private static int proxyReadTimeout;
    private static boolean disableSslVerification;
    private static boolean addProxyErrorDetailsToResponse;

    public static void load(String path) throws IOException {
        Properties configs = new Properties();
        configs.load(Files.newInputStream(Paths.get(path)));
        proxyUrl = configs.getProperty("proxyUrl");
        proxyConnectTimeout = Integer.parseInt(configs.getProperty("proxyConnectTimeout", "" + Integer.MAX_VALUE));
        proxyReadTimeout = Integer.parseInt(configs.getProperty("proxyReadTimeout", "" + Integer.MAX_VALUE));
        bufferSize = Integer.parseInt(configs.getProperty("bufferSize", "1024"));
        port = Integer.parseInt(configs.getProperty("port", "0"));
        threadPoolSize = Integer.parseInt(configs.getProperty("threadPoolSize", "200"));
        disableSslVerification = Boolean.parseBoolean(configs.getProperty("disableSslVerification", "false"));
        addProxyErrorDetailsToResponse = Boolean.parseBoolean(configs.getProperty("addProxyErrorDetailsToResponse", "false"));
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

    public static int getProxyConnectTimeout() {
        return proxyConnectTimeout;
    }

    public static int getProxyReadTimeout() {
        return proxyReadTimeout;
    }

    public static boolean shouldDisableSslVerification() {
        return disableSslVerification;
    }

    public static boolean shouldAddProxyErrorDetailsToResponse() {
        return addProxyErrorDetailsToResponse;
    }

}
