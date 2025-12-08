package com.globalsoft.daemon;

class NormalWorker implements Runnable {

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Executing the Normal Thread....");
        }
    }
}

class DaemonWorker implements Runnable {
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Executing the Daemon Thread....");
        }
    }
}

public class App {
    public static void main(String[] args) {
        //JVM does not kill user/worker thread !!!
        //this is called the foreground, high priority
        Thread t1 = new Thread(new NormalWorker());

        //JVM terminate daemon thread if no user/worker thread is present
        Thread t2 = new Thread(new DaemonWorker());
        t2.setDaemon(true);
        System.out.println(t1.isDaemon());
        System.out.println(t2.isDaemon());
        t1.start();
        t2.start();

    }
}
