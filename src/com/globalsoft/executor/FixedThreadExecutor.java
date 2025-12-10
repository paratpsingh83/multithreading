package com.globalsoft.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Work implements Runnable {
    private int id;

    public Work(int id) {
        this.id = id;
    }

    @Override
    public void run() {

        System.out.println("Task with id: " + id + " is in work-thread id: " + Thread.currentThread().getId());

        long duration = (long) (Math.random() * 5);
        try {
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }
}

public class FixedThreadExecutor {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 100; i++) {
            executor.execute(new Work(i + 1));
        }
        // we prevent the executor to execute ny further tasks
        executor.shutdown();

        // terminate actual (running) task

        try {
            if (!executor.awaitTermination(100, TimeUnit.MILLISECONDS)) {
              //  executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
