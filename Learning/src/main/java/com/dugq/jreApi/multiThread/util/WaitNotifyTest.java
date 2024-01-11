package com.dugq.jreApi.multiThread.util;

import com.dugq.ThreadUtil;
import com.dugq.pojo.ObjectUtil;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;


/**
 * Created by dugq on 2023/12/26.
 */
public class WaitNotifyTest {

    final Object obj = new Object();
    CountDownLatch countDownLatch = new CountDownLatch(1);
    ExecutorService executorService = ThreadUtil.getThreadPool("synchronized-thread");

    @Test
    public void testNormal() throws InterruptedException {
        ObjectUtil.printSynchronizedState(obj);
        executorService.execute(()->{
            synchronized (obj){
                ObjectUtil.printSynchronizedState(obj);
                ThreadUtil.sleep(4);
            }
        });
        executorService.execute(()->{
            synchronized (obj){
                ObjectUtil.printSynchronizedState(obj);
            }
        });
        new CountDownLatch(1).await();
    }

    // wait 会使锁直接升级为重量级锁
    @Test
    public void testWaitObjectState() throws InterruptedException {
        for (int i =0; i< 1; i++){
            executorService.execute(this::thread);
        }
        countDownLatch.await();
        ThreadUtil.sleep(1);
        ObjectUtil.printSynchronizedState("no block ",obj);
        synchronized (obj){
            obj.notify();
        }
        ThreadUtil.sleep(2);
    }

    public void thread()  {
        ObjectUtil.printSynchronizedState("init:",obj);
        synchronized (obj){
            ObjectUtil.printSynchronizedState(" got block before",obj);
            try {
                countDownLatch.countDown();
                obj.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ObjectUtil.printSynchronizedState("wait got block again",obj);
        }
        ObjectUtil.printSynchronizedState("wait release block",obj);
    }

}
