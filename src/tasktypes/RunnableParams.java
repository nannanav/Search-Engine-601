package tasktypes;

import taskstatusmanager.TaskStatus;

public interface RunnableParams {
    public String GetKey();
    public TaskStatus getTaskStatus();
}
