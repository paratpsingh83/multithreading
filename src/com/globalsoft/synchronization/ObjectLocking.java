package com.globalsoft.synchronization;

public class ObjectLocking {
    public synchronized void instanceMethod() {
        System.out.println(Thread.currentThread().getName() + "entered instanceMethod...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + " finish instanceMethod...");

    }
}

class App {
    public static void main(String[] args) {


        var object1 = new ObjectLocking();
        var object2 = new ObjectLocking();

        Runnable task1 = object1::instanceMethod;
        Runnable task2 = object2::instanceMethod;

        new Thread(task1," First Thread ").start();
        new Thread(task2," Second Thread ").start();


    }
}
