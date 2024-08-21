package taskmanager;

import java.util.ArrayList;
import java.util.List;

public class ThreadPool {

    int limit;
    TaskQueue queue = new TaskQueue();
    List<Worker> workers;


    public ThreadPool(TaskQueue queue) {
        new ThreadPool(queue, 4);   //default size 4
    }

    public ThreadPool(TaskQueue queue, int limit) {
        this.limit = limit;
        workers = new ArrayList<Worker>(limit);
        this.queue = queue;
        start();
    }

    private void start() {
        for (int i = 0; i < limit; i++) {
            Worker worker = new Worker(i, queue);
            worker.start();
            System.out.println("worker started");
            workers.add(worker);
        }
        System.out.println("all workers started");
    }


    public void End() throws InterruptedException {
        System.out.println("End");
        for (Worker worker : workers) {
            worker.interrupt();
//            System.out.println("is interrupted" + worker.isInterrupted());
//            System.out.println("is alive: " + worker.isAlive());
        }

        for (Worker worker : workers) {
            worker.join();
        }
    }
}
