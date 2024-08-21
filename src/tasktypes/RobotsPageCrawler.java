package tasktypes;

import taskmanager.TaskQueue;
import taskstatusmanager.TaskStatus;
import taskstatusmanager.TaskStatusMap;

import java.io.*;
import java.net.*;
import java.net.http.*;

public class RobotsPageCrawler implements Runnable {
    public static final String robotsUrl = "/robots.txt";
    private static final String UserAgentString = "User-agent: ";
    private static final String UserAgentVal = "*";
    private static final String AllowString = "Allow:";
    private final String domain;
    TaskQueue tasks;
    public RobotsPageCrawler(RobotsPageCrawlerParams params) {
        this.tasks = params.tasks;
        this.domain = params.domain;
    }

    public void run() {
        crawlRobotsPage();
    }

    private void crawlRobotsPage() {
        System.out.println("Crawling robots page...");
        try {
            String fullRobotsUrl = domain + robotsUrl;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(fullRobotsUrl))
                    .GET()
                    .build();
            HttpResponse<InputStream> response = TaskUtils.getClient().send(request, HttpResponse.BodyHandlers.ofInputStream());
            System.out.printf("Crawler got the following response status code from %s: %d\n", fullRobotsUrl, response.statusCode());
//            System.out.println("Crawler got the following response: " + response.body());

            String UserAgent = "";
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body()))) {
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    if (line.startsWith(UserAgentString)) {
                        UserAgent = line.substring(UserAgentString.length());
                    }

                    if (UserAgent.equals(UserAgentVal)) {
                        if (line.startsWith(AllowString)) {
                            line = line.substring(AllowString.length()).replace(" ", "");
                            String fullUrl = domain + line;
                            if (utils.UrlUtils.IsValidUrl(fullUrl)) {
                                new Crawler.CrawlerParams(domain, fullUrl).AddToTasks(tasks);
                            }
                        }
                    }
                }
            }
            System.out.println("robots page crawler domain: " + domain);
            //sitemap?? todo
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            TaskStatusMap.Complete(domain+robotsUrl);
        }
    }

    public static class RobotsPageCrawlerParams implements RunnableParams {
        private final String domain;
        private final String fullUrl;
        TaskQueue tasks;

        public RobotsPageCrawlerParams(TaskQueue tasks, String domain, String fullUrl) {
            this.tasks = tasks;
            this.domain = domain;
            this.fullUrl = fullUrl;
        }

        @Override
        public String GetKey() {
            return domain+robotsUrl;
        }

        @Override
        public TaskStatus getTaskStatus() {
            return TaskStatus.ToRobotsCrawl;
        }
    }
}
