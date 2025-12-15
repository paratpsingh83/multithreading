package com.globalsoft.synchronizedcollections.cyclicbarrier;

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

    }

}
