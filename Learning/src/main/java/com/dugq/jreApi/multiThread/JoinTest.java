package com.dugq.jreApi.multiThread;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by dugq on 2024/1/24.
 */
public class JoinTest {

    @Test
    public void testJoin() throws Exception {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        thread.join();
    }

    @Test
    public void testFuture() throws InterruptedException, ExecutionException {
        FutureTask<Integer> task = new FutureTask<>(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },1);
        Thread thread = new Thread(task);
        thread.start();
        Integer o = task.get();
        System.out.println(o);
    }

}
