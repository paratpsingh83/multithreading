package com.globalsoft.volatileexm;

class Worker implements Runnable {

    // it will be stored in the main memory
    // 1.) variable can be stored on the main memory without the volatile keyword
    // 2.) both of the threads may use the same cache !!!
    private volatile boolean terminated;

    @Override
    public void run() {
        while (!terminated) {
            System.out.println("Working class is running...");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isTerminated() {
        return terminated;
    }

    public void setTerminated(boolean terminated) {
        this.terminated = terminated;
    }
}

public class Volatile {

    public static void main(String[] args) {

        Worker worker = new Worker();
        Thread t1 = new Thread(worker);
        t1.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        worker.setTerminated(true);
        System.out.println("Algorithm is terminated...");
    }

}
