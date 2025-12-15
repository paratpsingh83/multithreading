package com.globalsoft.synchronizedcollections.latch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Worker implements Runnable {

    private int id;

    private CountDownLatch latch;

    public Worker(int id, CountDownLatch latch) {
        this.id = id;
        this.latch = latch;
    }

    @Override
    public void run() {
        doWork();
        latch.countDown();
    }

    private void doWork() {
        try {
            System.out.println("Thread with ID: " + this.id + " starts working...");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

public class Latch {

    public static void main(String[] args) {

        CountDownLatch latch = new CountDownLatch(5);

        /*"Always use a CountDownLatch with a count equal to or less than the number of tasks — never more,
         otherwise the application will get stuck waiting."
        */
//        CountDownLatch latch = new CountDownLatch(10);

        ExecutorService service = Executors.newSingleThreadExecutor();

        for (int i = 0; i < 5; i++) {
            service.execute(new Worker(i, latch));
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        System.out.println("All task have been finished...");
        service.shutdown();
    }
}
