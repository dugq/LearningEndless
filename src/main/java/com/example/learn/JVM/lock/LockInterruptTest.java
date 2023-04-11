package com.example.learn.JVM.lock;

import com.example.util.ThreadUtil;
import org.junit.Test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by dugq on 2023/4/6.
 */
public class LockInterruptTest {

    // 在lock方法等待中的线程被中断，在线程被唤起时可继续执行，可通过isInterrupted方法获取中断状态
    @Test
    public void test(){
        new Thread(()->{
            System.out.println("t1 start");
            System.out.println("is interrupt 1: "+Thread.currentThread().isInterrupted());
            Thread.currentThread().interrupt();
            System.out.println("is interrupt 2: "+Thread.currentThread().isInterrupted());
            System.out.println("t1 end");
        }).start();
        Thread t2 = new Thread(()->{
            System.out.println("t2 start");
            while (!Thread.currentThread().isInterrupted()){

            }
            System.out.println("t2 interrupt");
        });
        t2.start();
        ThreadUtil.sleep(3);
        System.out.println("----");
        t2.interrupt();
        ThreadUtil.sleep(1);
        System.out.println("main end");
    }

    @Test
    public void testLockInterrupt() {
        Lock lock = new ReentrantLock(false);
        lock.lock();
        Thread t = new Thread(()->{
            System.out.println("thread start");
            lock.lock();
            if (Thread.currentThread().isInterrupted()){
                System.out.println("Thread interrupted");
                return;
            }
            System.out.println("完蛋了");
        });
        t.start();
        ThreadUtil.sleep(1);
        t.interrupt();
        System.out.println("interrupted thread");
        ThreadUtil.sleep(2);
        System.out.println("unlock");
//        lock.unlock();
        ThreadUtil.sleep(300);
    }

    @Test
    public void testLockInterrupt2() {
        Lock lock = new ReentrantLock(false);
        lock.lock();
        Thread t = new Thread(()->{
            System.out.println("thread start");
            try {
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("完蛋了");
        });
        t.start();
        ThreadUtil.sleep(10);
        t.interrupt();
        System.out.println("interrupted thread");
        ThreadUtil.sleep(2);
        System.out.println("unlock");
//        lock.unlock();
        ThreadUtil.sleep(300);
    }
}
