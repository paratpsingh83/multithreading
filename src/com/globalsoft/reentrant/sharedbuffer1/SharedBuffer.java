package com.globalsoft.reentrant.sharedbuffer1;

import java.util.LinkedList;
import java.util.List;


//This example Consumer and producer using LinkedList & synchronized
public class SharedBuffer {

    private List<Integer> buffer = new LinkedList<>();

    private int capacity=5;

    public synchronized void produce() throws InterruptedException {
        if (buffer.size() == capacity) {
            System.out.println("Buffer full, producer waiting...");
            wait();
        }
        System.out.println("Adding items the producer...");
        for (int i = 0; i < capacity; i++) {
            buffer.add(i);
            System.out.println("Added value: " + i);
        }
        // wake up the consumer
        notify();

    }

    public synchronized void consume() throws InterruptedException {
        if (buffer.size() < capacity) {
            System.out.println("Buffer not full yet, consumer waiting... ");
            wait();
        }

        while (!buffer.isEmpty()) {
            int item = buffer.remove(0);
            System.out.println("Consumer remove: " + item);
            Thread.sleep(300);
        }

        //wake up the consumer
        notify();

    }
}

class Consumer implements Runnable {
    private SharedBuffer sharedBuffer;

    public Consumer(SharedBuffer sharedBuffer) {
        this.sharedBuffer = sharedBuffer;
    }

    @Override
    public void run() {

        try {
            while (true) {
                this.sharedBuffer.consume();
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);

        }
    }
}

class Producer implements Runnable {
    private SharedBuffer sharedBuffer;

    public Producer(SharedBuffer sharedBuffer) {
        this.sharedBuffer = sharedBuffer;
    }

    @Override
    public void run() {

        try {
            while (true) {
                this.sharedBuffer.produce();
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);

        }
    }
}


class App1 {
    public static void main(String[] args) {
        var sharedBuffer = new SharedBuffer();

        Thread t1 = new Thread(new Producer(sharedBuffer));
        Thread t2 = new Thread(new Consumer(sharedBuffer));

        t1.start();
        t2.start();

    }

}
