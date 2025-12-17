package com.globalsoft.synchronizedcollections.blockingqueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

class FirstWorker implements Runnable {

    private BlockingQueue<Integer> queue;

    public FirstWorker(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        int counter = 0;
        while (true) {
            try {
                queue.put(counter);
                System.out.println("Putting the item in the queue " + counter);
                counter++;
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class SecondWorker implements Runnable {
    private BlockingQueue<Integer> queue;

    public SecondWorker(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                int counter = queue.take();
                System.out.println("Taking the item from the queue " + counter);
                Thread.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

public class ArrayBlockingQueueExam {

    /*
     * Queue has a FIFO structure (first in first out) but it is not
     * a synchronized data structure !!!
     *
     * BlockingQueue -> an interface that represents a queue that is thread safe
     * Put items or take items from it ...
     *
     * For example: one thread putting items into the queue and another thread
     * taking items from it at the same time !!!
     *
     * We can do the producer-consumer pattern !!!
     *
     * put()  putting items to the queue
     * take() taking items from the queue
     */

    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);

        FirstWorker firstWorker = new FirstWorker(queue);
        SecondWorker secondWorker = new SecondWorker(queue);

        new Thread(firstWorker).start();
        new Thread(secondWorker).start();
    }
}
