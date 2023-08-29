package com.example.learn.jreApi.multiThread.pool;

import org.checkerframework.checker.units.qual.A;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dugq on 2023/4/26.
 */
public class ThreadPoolTest {

    public void testPool(){
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 50,
                10, TimeUnit.HOURS,
                new ArrayBlockingQueue<>(100),
                threadFactory, new ThreadPoolExecutor.AbortPolicy());
        executor.execute(()->{});
        Future<Integer> future = executor.submit(() -> 1);
        executor.getActiveCount();
        executor.getCompletedTaskCount();
        executor.getPoolSize();
        executor.getCorePoolSize();
        executor.getMaximumPoolSize();
        executor.getLargestPoolSize();
        executor.getTaskCount();
    }

    ThreadFactory threadFactory = new ThreadFactory() {
        private AtomicInteger index = new AtomicInteger();
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("dugq-test" + index.addAndGet(1));
            return thread;
        }
    };

}
