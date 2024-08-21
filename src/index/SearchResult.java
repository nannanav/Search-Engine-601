package index;

import java.io.Serializable;
import java.util.List;

public class SearchResult implements Serializable {
    public int id; //primary key
    public String PageTitle;
    public String OriginalPageLink; //online page
    public String DiskPageLink;
    public String TextDescription; //a short text description with highlighted words matching the search. The search feature
    // should not require exact matches; instead, allow fuzzy searching, a.k.a. approximate string matching

    public String JsonMarshal() {
        return String.format("""
                {"id":%d,"title":"%s","original page link":"%s","disk page link":"%s","text description":"%s"}
                """,
                id, PageTitle, OriginalPageLink, DiskPageLink, TextDescription);
    }

    public static String JsonMarshal(List<SearchResult> searchResults) {
        StringBuilder format = new StringBuilder("[");
        for (int i = 0; i < searchResults.size(); i++) {
            SearchResult searchResult = searchResults.get(i);
            format.append(searchResult.JsonMarshal());
            if (i < searchResults.size() - 1) {
                format.append(",");
            }
        }
        format.append("]");
        return format.toString();
    }
}
