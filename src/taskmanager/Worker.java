package taskmanager;

public class Worker extends Thread {
    private int id; //todo: domain specific thread?
    private final TaskQueue queue;
    public Worker(int id, TaskQueue queue) {
        this.id = id;
        this.queue = queue;
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Runnable task = queue.getTask();
                System.out.println("received task");
                task.run();
                System.out.println("completed");
            } catch (InterruptedException e) {
                break;
            } catch (RuntimeException e) {
                if (e.getCause() instanceof InterruptedException) {
                    break;
                }
                throw e;
            }
        }
        System.out.println("Worker interrupted");
        System.out.println("SHUTTING DOWN");
    }
}
