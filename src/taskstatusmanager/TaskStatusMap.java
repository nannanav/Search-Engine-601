package taskstatusmanager;

import taskmanager.TaskQueue;
import tasktypes.CompletedStatus;
import tasktypes.RunnableParams;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TaskStatusMap implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final String FileName = "TaskStatusMap.ser";
    private static TaskStatusMap instance;
    private Map<String, RunnableParams> taskStatusMap;
    public static TaskQueue tasks;
    private final static ReadWriteLock lock = new ReentrantReadWriteLock();

    static {
        createNewTaskStatusMap();
    }

    public static void Write(RunnableParams params) {
        lock.writeLock().lock();
        try {
            instance.taskStatusMap.put(params.GetKey(), params);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void Complete(String key) {
        lock.writeLock().lock();
        try {
            instance.taskStatusMap.put(key, CompletedStatus.GetInstance());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static boolean LoadTaskStatusMapFromFile() {
        try {
            instance = (TaskStatusMap) utils.FileUtils.LoadFromSerializedFile(FileName);
            return true;
        } catch (FileNotFoundException _) {
            return false;
        }
    }

    private static void createNewTaskStatusMap() {
        instance = new TaskStatusMap();
        instance.taskStatusMap = new HashMap<>();
    }

    public static void AddTasksFromStoredData(TaskQueue tasks) {
        TaskStatusMap.tasks = tasks;
        instance.taskStatusMap.forEach((_, value) -> {
            if (value.getTaskStatus().isTaskRequired()) {
                try {
                    tasks.addTask(value.getTaskStatus().getTaskType().getDeclaredConstructor(value.getTaskStatus().getTaskParamsType()).newInstance(value));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static void StoreTaskStatusMapInFile() {
        utils.FileUtils.StoreSerializedInFile(FileName, instance);
    }
}
