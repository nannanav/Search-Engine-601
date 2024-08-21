package tasktypes;

import index.Index;
import index.SearchResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import taskmanager.TaskQueue;
import taskstatusmanager.TaskStatus;
import taskstatusmanager.TaskStatusMap;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Indexer implements Runnable {
    IndexerParams params;
    public Indexer(IndexerParams params) {
        this.params = params;
    }

    public void run() {
        //todo grab all links
        System.out.println("Indexer started");
        ArrayList<String> strings = new ArrayList<>();
        File file = new File(params.fileName);
        try {
            Document doc = Jsoup.parse(file);
//            Jsoup.parse(InputStream.nullInputStream(), "utf-8", link); todo?
            doc.stream().forEach(ele -> {
//                System.out.println("url: " + params.link);
//                System.out.println("tag name: " + ele.tagName());
                List<TextNode> textNodes = ele.textNodes();
                textNodes.forEach(textNode -> {
                    if (!textNode.isBlank()) {
                        strings.add(textNode.toString().strip());
                    }
                });
            });
            saveToIndex(doc.title(), strings);
            TaskStatusMap.Complete(params.GetKey());
        } catch (IOException e) {
            System.err.println(params.fileName);
            throw new RuntimeException(e);
        }
    }

    void saveToIndex(String title, ArrayList<String> strings) {
        strings.forEach(string -> {
            SearchResult result = new SearchResult();
            result.PageTitle = title;
            result.OriginalPageLink = params.link;
            result.DiskPageLink = params.fileName;
            result.TextDescription = string;
            Index.indexData(result);
        });
    }

    public static class IndexerParams implements RunnableParams, Serializable {
        String fileName;
        String link;

        IndexerParams(String fileName, String link) {
            this.fileName = fileName;
            this.link = link;
        }

        @Override
        public String GetKey() {
            return link;
        }

        @Override
        public TaskStatus getTaskStatus() {
            return TaskStatus.ToIndexer;
        }

        public void AddToTasks(TaskQueue tasks) {
            tasks.addTask(new Indexer(this));
            TaskStatusMap.Write(this);
        }
    }
}
