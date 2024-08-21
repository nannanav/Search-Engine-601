package tasktypes;

import taskstatusmanager.TaskStatus;

import java.io.Serializable;

public class CompletedStatus implements RunnableParams, Serializable {
    private static CompletedStatus instance;

    public static CompletedStatus GetInstance() {
        if (instance == null) {
            instance = new CompletedStatus();
        }
        return instance;
    }

    @Override
    public String GetKey() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public TaskStatus getTaskStatus() {
        return TaskStatus.Completed;
    }
}
