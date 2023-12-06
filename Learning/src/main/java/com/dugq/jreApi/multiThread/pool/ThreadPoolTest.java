package com.dugq.jreApi.multiThread.pool;

import com.dugq.ThreadUtil;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dugq on 2023/4/26.
 */
public class ThreadPoolTest {

    ThreadFactory threadFactory = new ThreadFactory() {
        private AtomicInteger index = new AtomicInteger();
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("dugq-test" + index.addAndGet(1));
            return thread;
        }
    };

    ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 50, 10, TimeUnit.HOURS, new ArrayBlockingQueue<>(100), threadFactory, new ThreadPoolExecutor.AbortPolicy());

    public void testPool(){
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

    @Test
    public void testScheduleExecutor() throws InterruptedException {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(10, threadFactory, new ThreadPoolExecutor.AbortPolicy());
        scheduledThreadPoolExecutor.execute(()-> System.out.println("schedule thread pool execute "));
        Future<String> future = scheduledThreadPoolExecutor.submit(() -> {
            System.out.println("schedule thread pool submit; ");
            return "submit!";
        });

        scheduledThreadPoolExecutor.schedule(() -> {
            System.out.println("schedule thread pool schedule execute; ");
        },1,TimeUnit.SECONDS);

        ScheduledFuture<String> scheduleFuture = scheduledThreadPoolExecutor.schedule(() -> {
            System.out.println("schedule thread pool schedule submit; ");
            return "schedule submit!";
        }, 1, TimeUnit.SECONDS);

        scheduledThreadPoolExecutor.scheduleAtFixedRate(()->{
            System.out.println("schedule thread pool scedulle fixed execute; ");
        },1,1,TimeUnit.SECONDS);
        new CountDownLatch(1).await();
    }



    @Test
    public void testState(){
        executorTask(20);
        ThreadUtil.sleep(2);
        // shut down state
        executor.shutdown();
        // stop state
        // executor.shutdownNow();
        boolean flag = true;

        executorTask(1);
        while (true){
            if (executor.isShutdown() && flag) {
                System.out.println("shutDowned");
                flag = false;
            }
            if (executor.isTerminated()){
                System.out.println("stopped");
                break;
            }
        }
    }

    public void executorTask(int count){
        try {
            for (int i = 0; i< count; i++){
                int finalI = i;
                executor.execute(()->testFun(finalI));
            }
        }catch (RejectedExecutionException e){
            System.err.println(e.getMessage());
        }

    }

    public void testFun(int task){
        ThreadUtil.printThreadInfo("started "+task + " ");
        long start = System.currentTimeMillis();
        ThreadUtil.sleep(10);
        ThreadUtil.printThreadInfo("ending "+task + " sleep "+ (System.currentTimeMillis()-start)/1000 + " s");
    }

}
