package taskmanager;

import java.util.LinkedList;
import java.util.Queue;

public class TaskQueue {

    Queue<Runnable> tasks = new LinkedList<Runnable>();

    public synchronized void addTask(Runnable task) {
        tasks.add(task);
        notify();
    };

    public synchronized Runnable getTask() throws InterruptedException {
        while (tasks.isEmpty()) {
            wait();
        }
        return tasks.poll();
    }

}
