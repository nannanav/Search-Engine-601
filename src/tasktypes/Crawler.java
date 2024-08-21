package tasktypes;

import taskmanager.TaskQueue;
import taskstatusmanager.TaskStatus;
import taskstatusmanager.TaskStatusMap;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class Crawler implements Runnable {
    CrawlerParams params;

    public Crawler(CrawlerParams params) {
        this.params = params;
    }

    public void run() {
        try {
            System.out.println("Crawler started");
            System.out.println("crawling " + params.fullUrl);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(params.fullUrl))
                    .GET()
                    .build();
            HttpResponse<InputStream> response = TaskUtils.getClient().send(request, HttpResponse.BodyHandlers.ofInputStream());
            if (response.statusCode() == 404) {
                System.out.printf("obtained 404 from %s\n", params.fullUrl);
                return;
            }
            String folder1Name = "data";
            String fileName = String.format("%s/%s", folder1Name, params.fullUrl);
            System.out.println("fileName: " + fileName);
            File file = createDir(fileName);
            fileName = file.getPath();
            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = response.body().read(buffer)) != -1) {
                    fos.write(buffer, 0, length);
                }
            } finally {
                response.body().close();
            }
            new Indexer.IndexerParams(fileName, params.fullUrl).AddToTasks(TaskStatusMap.tasks);
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private File createDir(String dirName) throws IOException {
        String[] splitString = dirName.split("/");
        List<String> splitStringProper = new ArrayList<>();
        File file = null;
        for (String s : splitString) {
            if (!(s.isEmpty() || s.equals("http:") || s.equals("https:"))) {
                splitStringProper.add(s);
            }
        }
        dirName = "";
        for (int i = 0; i < splitStringProper.size(); i++) {
            if (i < splitStringProper.size() - 1) {
                dirName += splitStringProper.get(i) + "/";
                file = new File(dirName);
                file.mkdir();
            } else {
                dirName += splitStringProper.get(i) + ".html";
                file = new File(dirName);
                System.out.println("creating file: " + dirName);
                boolean result = file.createNewFile();
                System.out.println("file created: " + result);
            }
        }
        return file;
    }

    public static class CrawlerParams implements RunnableParams, Serializable {
//        private final transient TaskQueue tasks;
        private final String domain; //todo: make sure it runs in one thread
        private final String fullUrl;

        CrawlerParams(String domain, String fullUrl) {
//            this.tasks = tasks;
            this.domain = domain;
            this.fullUrl = fullUrl;
        }

        @Override
        public String GetKey() {
            return fullUrl;
        }

        @Override
        public TaskStatus getTaskStatus() {
            return TaskStatus.ToCrawl;
        }

        public void AddToTasks(TaskQueue tasks) {
            tasks.addTask(new Crawler(this));
            TaskStatusMap.Write(this);
        }
    }
}
