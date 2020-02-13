package ir.mkay.simplehttpproxy;

import com.sun.net.httpserver.HttpServer;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleHttpProxyApplication {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Please provide config file path.");
            return;
        }
        disableSslVerification();
        Configuration.load(args[0]);
        ExecutorService executorService = Executors.newFixedThreadPool(Configuration.getThreadPoolSize());
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", Configuration.getPort()), 0);
        server.setExecutor(executorService);
        server.createContext("/", new RequestHandler());
        server.start();
        System.out.printf("Server started on port %d with %d thread(s).\n", Configuration.getPort(), Configuration.getThreadPoolSize());
    }

    public static void disableSslVerification() {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Everything is valid
            HttpsURLConnection.setDefaultHostnameVerifier((a, b) -> true);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

}
