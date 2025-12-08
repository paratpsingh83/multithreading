package com.globalsoft.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockExample {

    private static int counter = 0;

    private static Lock lock = new ReentrantLock(true);

    public static void increment() {

//        synchronized (LockExample.class){
//
//        }

        try {
            lock.lock();
            counter++;
        } finally {
            lock.unlock();
        }

    }

//    public static void unlock() {
//
//    }

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                increment();
            }

        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                increment();
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

        System.out.println("Counter: " + counter);
    }
}
