package com.globalsoft.semaphore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

//singleton pattern
enum Downloader {
    INSTANCE;
    private Semaphore semaphore = new Semaphore(3, true);

    public void download() {
        try {
            semaphore.acquire();
            downloadData();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            semaphore.release();
        }
    }

    private void downloadData() {
        try {
            System.out.println("Downloading data from the web...");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class App {
    /*
     * It is used to control access to shared resource
     * that uses a counter variable
     *
     *             //semaphore maintains a set of permits
     *    -acquire() ...if a permit id available then takes it
     *    -release() ...adds a permit
     *
     *         Semaphore just keep a count of number of permits available
     *         new Semaphore (int permits, boolean fair)
     */

    public static void main(String[] args) {
        //Create multiple threads-executors
        ExecutorService service = Executors.newCachedThreadPool();
        for (int i = 0; i < 12; i++) {
            service.execute(new Runnable() {
                @Override
                public void run() {
                    Downloader.INSTANCE.download();
                }
            });
        }
    }
}
