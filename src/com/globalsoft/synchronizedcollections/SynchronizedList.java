package com.globalsoft.synchronizedcollections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SynchronizedList {
    public static void main(String[] args) {

        /*
         * add() and remove() method are synchronized
         * intrinsic lock - not that efficient because thread have to wait for each other even when the wait to execute independent methods (operations)
         * solution - use CONCURRENT COLLECTION
         */

//        List<Integer> list = new ArrayList<>();
        List<Integer> list = Collections.synchronizedList(new ArrayList<>());
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 90000; i++) {
                    list.add(i);
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 90000; i++) {
                    list.add(i);
                }
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Size of array: " + list.size());
    }
}
