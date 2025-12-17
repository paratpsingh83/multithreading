package com.globalsoft.synchronizedcollections.blockingqueue;

import java.util.concurrent.LinkedBlockingQueue;

public class LinkedBlockingQueueExample {

    public static void main(String[] args) {
//        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(10);
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();

        Runnable producer = () -> {
            try {
                int i = 0;
                while (true) {
                    String s = "Item " + i++;
                    queue.put(s);
                    System.out.println("Produced: " + s);
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Runnable consumer = () -> {
            try {
                while (true) {
                    String s = queue.take();
                    System.out.println("Consumed: " + s);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        new Thread(producer).start();
        new Thread(consumer).start();

    }
}
