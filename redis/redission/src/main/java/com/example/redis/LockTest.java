package com.example.redis;

import org.junit.jupiter.api.Test;
import org.redisson.RedissonLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by dugq on 2023/10/16.
 */
@SpringBootTest
public class LockTest {
    @Resource
    private RedissonClient redissonClient;
    CountDownLatch countDownLatch = new CountDownLatch(10);

    @Test
    public void testLockSimple() throws InterruptedException {
        RedissonLock lock = (RedissonLock)redissonClient.getLock("lock:test");
        boolean locked = lock.tryLock();
        if (!locked){
            System.out.println("lock error!");
            return;
        }
        TimeUnit.MINUTES.sleep(5);
        lock.unlock();
    }

    @Test
    public void testLockRetry() throws InterruptedException {
        RLock lock = redissonClient.getLock("lock:test");
        boolean locked = lock.tryLock(100, TimeUnit.SECONDS);
        if (!locked){
            System.out.println("lock error!");
            return;
        }
        TimeUnit.MINUTES.sleep(5);
        lock.unlock();
    }

    //自旋
    @Test
    public void testSpin() throws InterruptedException {
        RLock lock = redissonClient.getSpinLock("lock:spin");
        boolean locked = lock.tryLock(100,10, TimeUnit.SECONDS);
        if (!locked){
            System.out.println("lock error!");
            return;
        }
        TimeUnit.MINUTES.sleep(5);
        lock.unlock();
    }

    @Test
    public void testLeaseTime() throws InterruptedException {
        RLock lock = redissonClient.getLock("lock:test");
        boolean locked = lock.tryLock(100,10, TimeUnit.SECONDS);
        if (!locked){
            System.out.println("lock error!");
            return;
        }
        TimeUnit.SECONDS.sleep(15);
        lock.unlock();
    }



    @Test
    public void test() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i =0 ;i < 10; i++){
            executorService.execute(this::testLock);
        }
        countDownLatch.await();
    }


    private void testLock() {
        RLock lock = redissonClient.getLock("test");
        boolean result = false;
        try{
             result = lock.tryLock(10, TimeUnit.SECONDS);
             if (!result){
                 System.out.println("get non lock!");
                 return;
             }
            System.out.println("get lock!"+Thread.currentThread().getName());
            Thread.sleep(1200);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            if (result){
                lock.unlock();
                System.out.println("release lock!"+Thread.currentThread().getName());
            }
            countDownLatch.countDown();
        }
    }
}
