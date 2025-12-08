package com.globalsoft.lock.ConProUsingReentrant;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class LockWorker {

    private Lock lock = new ReentrantLock(true);

    private Condition condition = lock.newCondition();

    public void producer() throws InterruptedException {
        lock.lock();
        System.out.println("Producer method...");
        //wait();
        condition.await();
        System.out.println("Again the producer method...");
        lock.unlock();
    }

    public void consume() throws InterruptedException {
        //we want to make sure that we start with the producer()
        Thread.sleep(2000);
        lock.lock();
        System.out.println("Consumer method...");
        Thread.sleep(3000);
        //notify()
        condition.signal();
        lock.unlock();
    }

}

public class App {
    public static void main(String[] args) {
        LockWorker worker = new LockWorker();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    worker.producer();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    worker.consume();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        t2.start();


        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
