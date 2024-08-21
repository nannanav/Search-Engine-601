package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import index.Index;
import index.SearchResult;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

public class SearchHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            System.out.println("search called");
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> queryParams = ServerUtils.parseQueryParams(query);
            String searchQuery = queryParams.get("q");
            String jsonData = "";
            String html = Files.readString(Paths.get("src/handlers/search.html"));

            if (searchQuery != null && !searchQuery.isEmpty()) {
                ArrayList<SearchResult> searchResults = Index.search(searchQuery);
                jsonData = SearchResult.JsonMarshal(searchResults);
            }
            html = html.replace("{{defaultValue}}", searchQuery);
            html = html.replace("{{searchResult}}", jsonData);

            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, html.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(html.getBytes());
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
            String response = "Internal Server Error!";
            exchange.sendResponseHeaders(500, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}