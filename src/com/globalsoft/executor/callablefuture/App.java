package com.globalsoft.executor.callablefuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

class Processor implements Callable<String> {
    private int id;

    public Processor(int id) {
        this.id = id;
    }

    @Override
    public String call() throws Exception {
        Thread.sleep(2000);
        return "id: " + id;
    }
}

public class App {
    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(2);

        List<Future<String>> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            Future<String> future = executor.submit(new Processor(i + 1));
            list.add(future);

        }
        for (Future<String> f : list) {
            try {
                System.out.println(f.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
