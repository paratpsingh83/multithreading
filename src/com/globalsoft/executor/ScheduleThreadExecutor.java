package com.globalsoft.executor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class StockMarketUpdater implements Runnable {
    @Override
    public void run() {
        System.out.println("Updating the downloading stock related data from web... ");
    }
}

public class ScheduleThreadExecutor {

    public static void main(String[] args) {

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(new StockMarketUpdater(), 100, 200, TimeUnit.MILLISECONDS);

    }
}
