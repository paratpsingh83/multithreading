package com.globalsoft.reentrant.sharedbuffer1;

class Process {
    public void produce() throws InterruptedException {

        synchronized (this) {
            System.out.println("Running the produce method...");
            wait();
            System.out.println("Again in the produce method");
        }
    }

    public void consume() throws InterruptedException {


        Thread.sleep(1000);

        synchronized (this) {
            System.out.println("Running the Consume method");
            notify();
            //other operation are execute first
            System.out.println("After the notify() method call in the consume method...");
        }
    }
}

public class App {
    public static void main(String[] args) {
        var process = new Process();
        var t1 = new Thread(() -> {
            try {
                process.produce();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        var t2 = new Thread(() -> {
            try {
                process.consume();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        t1.start();
        t2.start();
    }
}
