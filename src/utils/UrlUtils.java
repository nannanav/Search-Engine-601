package utils;

public class UrlUtils {
    public static boolean IsValidUrl(String url) {
        return !url.endsWith("/api/js");
    }
}
