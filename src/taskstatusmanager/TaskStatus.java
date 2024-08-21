package taskstatusmanager;

import tasktypes.*;

public enum TaskStatus {
    ToRobotsCrawl {
        public boolean isTaskRequired() {
            return true;
        }
        public Class<? extends Runnable> getTaskType() {
            return RobotsPageCrawler.class;
        }
        public Class<? extends RunnableParams> getTaskParamsType() {
            return RobotsPageCrawler.RobotsPageCrawlerParams.class;
        }
    },
    ToCrawl {
        public boolean isTaskRequired() {
            return true;
        }
        public Class<? extends Runnable> getTaskType() {
            return Crawler.class;
        }
        public Class<? extends RunnableParams> getTaskParamsType() {
            return Crawler.CrawlerParams.class;
        }
    },
    ToIndexer {
        public boolean isTaskRequired() {
            return true;
        }
        public Class<? extends Runnable> getTaskType() {
            return Indexer.class;
        }
        public Class<? extends RunnableParams> getTaskParamsType() {
            return Indexer.IndexerParams.class;
        }
    },
    Completed {
        public boolean isTaskRequired() {
            return false;
        }
        public Class<? extends Runnable> getTaskType() {
            throw new RuntimeException("invalid task type. No task required.");
        }
        public Class<? extends RunnableParams> getTaskParamsType() {
            throw new RuntimeException("invalid task params type. No task required.");
        }
    };

    public abstract boolean isTaskRequired();

    public abstract Class<? extends Runnable> getTaskType();

    public abstract Class<? extends RunnableParams> getTaskParamsType();
}
