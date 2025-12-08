package com.globalsoft.priority;

class Task implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i <= 5; ++i) {
            System.out.println(Thread.currentThread().getName() + " count: " + i);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

public class App {
    public static void main(String[] args) {

        Thread low = new Thread(new Task(), "Low Priority Thread");
        Thread normal = new Thread(new Task(), "Normal Priority Thread");
        Thread high = new Thread(new Task(), "High Priority Thread");

        low.setPriority(Thread.MIN_PRIORITY);
        normal.setPriority(Thread.NORM_PRIORITY);
        high.setPriority(Thread.MAX_PRIORITY);

        low.start();
        normal.start();
        high.start();

    }
}
