package com.globalsoft.synchronization;

public class ThreadExample1 {

    private int counter1;
    private int counter2;

    private Object lock1 = new Object();
    private Object lock2 = new Object();

    public void increment1() {

        //execute operations before
        //Object Level locking
        synchronized (lock1) {
            counter1++;
        }
        //execute operations after
    }

    public void increment2() {

        //execute operations before
        synchronized (lock2) {
            counter2++;
        }
        //execute operations after
    }

    public void execute() {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                increment1();
            }

        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                increment2();
            }

        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Counter1: " + counter1);
        System.out.println("Counter2: " + counter2);
    }
}
