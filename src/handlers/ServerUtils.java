package handlers;

import com.sun.net.httpserver.HttpServer;
import taskmanager.ThreadPool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ServerUtils {

    private static HttpServer server;

    protected static ThreadPool pool;

    public static void startServer(ThreadPool threadPool) throws IOException {
        pool = threadPool;

        server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/hello", new HelloHandler());

        server.createContext("/search", new SearchHandler());

        server.createContext("/safestop", new SafeStopHandler());

        server.createContext("/script.js", new JavaScriptHandler());

        server.start();

        System.out.println("Server is listening on port 8080...");
    }

    public static void stopServer() {
        System.out.println("STOPPING SERVER...");
        server.stop(0);
    }

    public static Map<String, String> parseQueryParams(String query) {
        Map<String, String> queryParams = new HashMap<>();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    queryParams.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return queryParams;
    }
}
