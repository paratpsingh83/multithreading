package com.globalsoft;

class Runner1 implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Runner1 " + i);
        }
    }
}

class Runner2 implements Runnable {


    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Runner2 " + i);
        }
    }
}

public class App {
    public static void main(String[] args) {
        Runnable r1 = () -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("Runner1 " + i);
            }
        };

        Runnable r2 = () -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("Runner2 " + i);
            }
        };

        var t1 = new Thread(r1);
        var t2 = new Thread(r2);
        t1.start();
        t2.start();

    }
}
