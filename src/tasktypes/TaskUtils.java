package tasktypes;

import java.net.http.HttpClient;
import java.time.Duration;

public class TaskUtils {
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();

    public static HttpClient getClient() {
        return client;
    }
}
