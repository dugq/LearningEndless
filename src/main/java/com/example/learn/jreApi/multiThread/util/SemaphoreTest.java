package com.example.learn.jreApi.multiThread.util;

import com.example.util.ThreadUtil;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dugq on 2023/4/25.
 */
public class SemaphoreTest {
    ExecutorService executors = Executors.newFixedThreadPool(10);

    @Test
    public void test(){
        Semaphore semaphore = new Semaphore(2);
        AtomicInteger atomicInteger = new AtomicInteger(10);
        for (int i = 0; i< 10; i++){
            executors.submit(()->{
                int index = atomicInteger.getAndAdd(1);
                try {
                    semaphore.acquire();
                    ThreadUtil.sleep(3);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }finally {
                    semaphore.release();
                    System.out.println(index + " end");
                }
            });
        }
        ThreadUtil.sleep(100);
    }
}
