package com.globalsoft.synchronization;

public class ClassLocking {

    public static synchronized void instanceMethod() {

        System.out.println(Thread.currentThread().getName() + "entered instanceMethod...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + " finish instanceMethod...");

    }
}

class App1 {
    public static void main(String[] args) {

        Runnable task1 = ClassLocking::instanceMethod;
        Runnable task2 = ClassLocking::instanceMethod;

        new Thread(task1, " First Thread ").start();
        new Thread(task2, " Second Thread ").start();


    }
}
