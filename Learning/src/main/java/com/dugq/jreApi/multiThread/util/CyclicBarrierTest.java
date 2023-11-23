package com.dugq.jreApi.multiThread.util;

import com.dugq.ThreadUtil;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dugq on 2023/4/25.
 */
public class CyclicBarrierTest {
    ExecutorService executors = Executors.newFixedThreadPool(10);

    @Test
    public void test() throws InterruptedException {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(10);
        long l = System.currentTimeMillis();
        for (int i = 0; i< 10; i++){
            executors.submit(()->{
                ThreadUtil.sleep(RandomUtils.nextInt(1,10));
                System.out.println(System.currentTimeMillis() - l + " stated");
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(System.currentTimeMillis()-l + "  end");
            });
        }
        ThreadUtil.sleep(10);
    }


    volatile Thread thread;

    /**
     * 如果一个线程被中断，所有线程都中断
     * @throws InterruptedException
     */
    @Test
    public void testInterrupt() throws InterruptedException {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(10);
        long l = System.currentTimeMillis();
        AtomicInteger flag = new AtomicInteger();

        for (int i = 0; i< 10; i++){
            executors.submit(()->{
                // 让睡的最长的一个中断
                int ThreadIndex = flag.getAndAdd(1);
                try {
                    if (ThreadIndex == 5){
                        thread = Thread.currentThread();
                        throw new RuntimeException();
                    }else{
                        ThreadUtil.sleep(RandomUtils.nextInt(1,2));
                    }
                }finally {
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        System.out.println("interrupt "+ThreadIndex);
                        return;
                    }
                    System.out.println(thread + "  end");
                }
            });
        }
        ThreadUtil.sleep(6);
        //thread.interrupt();
        ThreadUtil.sleep(16);
    }
}
