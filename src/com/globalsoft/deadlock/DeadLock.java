package com.globalsoft.deadlock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadLock {

    private Lock lock1 = new ReentrantLock(true);
    private Lock lock2 = new ReentrantLock(true);


    public static void main(String[] args) {
        DeadLock deadLock = new DeadLock();

        //after java 8 it is possible to create thread like this
        new Thread(deadLock::worker1, "Worker1").start();
        new Thread(deadLock::worker2, "Worker2").start();

    }

    public void worker1() {
        lock1.lock();
        System.out.println("Worker1 acquires the lock1....");
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        lock2.lock();
        System.out.println("Worker1 acquires the lock2");
        lock1.unlock();
        lock2.unlock();
    }

    public void worker2() {
        lock2.lock();
        System.out.println("Worker2 acquires the lock2....");
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        lock1.lock();
        System.out.println("Worker2 acquires the lock1");
        lock1.unlock();
        lock2.unlock();
    }


//solution

//    public void worker1() {
//        lock1.lock();
//        System.out.println("Worker1 acquires the lock1....");
//        try {
//            Thread.sleep(300);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//        lock2.lock();
//        System.out.println("Worker1 acquires the lock2");
//        lock1.unlock();
//        lock2.unlock();
//    }
//
//
//
//
//    public void worker2() {
//        lock1.lock();
//        System.out.println("Worker2 acquires the lock1....");
//        try {
//            Thread.sleep(300);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//        lock2.lock();
//        System.out.println("Worker2 acquires the lock2");
//        lock1.unlock();
//        lock2.unlock();
//    }
}
