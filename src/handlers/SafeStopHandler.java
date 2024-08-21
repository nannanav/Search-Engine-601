package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import index.Index;
import taskstatusmanager.TaskStatusMap;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;

public class SafeStopHandler implements HttpHandler {
    @Override
    //currently works for all method types
    public void handle(HttpExchange exchange) throws IOException {
        try {
            System.out.println("Safe Stop Api called");
            String response1 = "Thank you for using this app! The app server will stop after saving data.!\n";
            exchange.sendResponseHeaders(200, 0);
            OutputStream os = exchange.getResponseBody();
            os.write(response1.getBytes());
            os.flush();
            os.close();

            ServerUtils.pool.End();

            System.out.println("StoreIndexInFile");
            Index.StoreIndexInFile();
            System.out.println("StoreTaskStatusMapInFile");
            TaskStatusMap.StoreTaskStatusMapInFile();

            ServerUtils.stopServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
