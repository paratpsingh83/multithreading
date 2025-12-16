package com.globalsoft.synchronizedcollections.cyclicbarrier;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class BarrierWorker implements Runnable {
    private int id;
    private Random random;
    private CyclicBarrier cyclicBarrier;




    public BarrierWorker(int id, CyclicBarrier cyclicBarrier) {
        this.id = id;
        this.random = new Random();
        this.cyclicBarrier = cyclicBarrier;
    }



    @Override
    public void run() {
        doWork();
    }

    private void doWork() {

        System.out.println("Thread with id " + this.id + " start the work...");
        try {
            Thread.sleep(random.nextInt(3000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            //when all the thread call await() this is when
            //the "barrier is broken"
            cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
        System.out.println("After the await()...");
    }

}

public class CyclicBarrierExample {
    /*
     * Latch -> a single thread can wait for other threads
     * CyclicBarrier -> multiple threads can wait for each other
     * A CyclicBarrier is used in situations where you want to create a group of
     * tasks to perform work in a concurrent manner + wait until they are all
     * finished before moving on to the next step
     *      --> something like join()
     *      --> something like CountDownLatch
     *
     * CountDownLatch: one-shot event
     * CyclicBarrier: it can be reused over and over again
     *      + cyclicBarrier has a barrier action: a runnable, that will run
     *        automatically when the count reaches 0 !!
     * new CyclicBarrier(N) --> N threads will wait for each other
     * WE CAN NOT REUSE LATCHES BUT WE CAN REUSE CyclicBarriers --> reset() !!!
     */

    public static void main(String[] args) {

        ExecutorService service = Executors.newFixedThreadPool(5);

        //5 is the number of parties(the number of Threads)
        CyclicBarrier barrier = new CyclicBarrier(5, new Runnable() {
            @Override
            public void run() {
                System.out.println("All Task have been finished...");
            }
        });
        for (int i = 0; i < 5; i++) {
            service.execute(new BarrierWorker(i + 1, barrier));
        }
        service.shutdown();

    }

}
