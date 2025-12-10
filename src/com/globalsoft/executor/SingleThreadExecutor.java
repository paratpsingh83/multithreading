package com.globalsoft.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Task implements Runnable {

    private int id;

    public Task(int id) {
        this.id = id;
    }

    @Override
    public void run() {

        System.out.println("Task with id: " + id + " is in work-thread id: " + Thread.currentThread().getName());

        long duration = (long) (Math.random() * 5);

        try {
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class SingleThreadExecutor {

    public static void main(String[] args) {


        ExecutorService executor = Executors.newSingleThreadExecutor();

        for (int i = 0; i < 5; i++) {
            executor.execute(new Task(i));
        }
        // we prevent the executor to execute ny further tasks
        executor.shutdown();

        // terminate actual (running) task

        try {
            if (!executor.awaitTermination(100, TimeUnit.MILLISECONDS)) {
//                  executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

    }
}
