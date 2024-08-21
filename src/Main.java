import index.Index;
import taskmanager.TaskQueue;
import taskmanager.ThreadPool;
import taskstatusmanager.TaskStatusMap;
import tasktypes.RobotsPageCrawler;

import static handlers.ServerUtils.startServer;
import static tasktypes.RobotsPageCrawler.robotsUrl;

public class Main {
    public static void main(String[] args) throws Exception {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!\n");

        boolean newProgress = processArgs(args);

        TaskQueue tasks = new TaskQueue();
        boolean successfullyLoaded = true;

        if (!newProgress) {
            successfullyLoaded &= TaskStatusMap.LoadTaskStatusMapFromFile();
            TaskStatusMap.AddTasksFromStoredData(tasks);
            successfullyLoaded &= Index.LoadIndexFromFile();
            System.out.println("loaded successfully: " + successfullyLoaded);
        }
        if (!successfullyLoaded) {
            for (String domain : SeedDomains.domains) {
                RobotsPageCrawler.RobotsPageCrawlerParams params = new RobotsPageCrawler.RobotsPageCrawlerParams(tasks, domain, domain+robotsUrl);
                tasks.addTask(new RobotsPageCrawler(params));
                TaskStatusMap.Write(params);
            }
            Index.CreateNewIndex();
        }
        System.out.println("tasks added");

        ThreadPool pool = new ThreadPool(tasks, 4);

        startServer(pool);
    }

     static boolean processArgs(String[] args) {
        boolean newProgress = false;
        for (String arg: args) {
            switch (arg) {
                case "new":
                    newProgress = true;
                    System.out.println("new progress requested");
                    break;
                case "dummy":
                    break;
            }
        }
        return newProgress;
    }
}
