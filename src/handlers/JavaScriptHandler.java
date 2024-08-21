package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

class JavaScriptHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String js = Files.readString(Paths.get("src/handlers/script.js"));
        exchange.getResponseHeaders().set("Content-Type", "application/javascript");
        exchange.sendResponseHeaders(200, js.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(js.getBytes());
        os.close();
    }
}
