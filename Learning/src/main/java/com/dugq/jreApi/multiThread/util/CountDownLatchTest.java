package com.dugq.jreApi.multiThread.util;

import com.dugq.ThreadUtil;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dugq on 2023/4/25.
 */
public class CountDownLatchTest {
    ExecutorService executors = Executors.newFixedThreadPool(10);

    @Test
    public void test() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i< 10; i++){
            executors.submit(()->{
                ThreadUtil.sleep(RandomUtils.nextInt(1,10));
                System.out.println("end");
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        countDownLatch.await();
        System.out.println("main end ");
    }

}
