package com.dugq.jreApi.multiThread;

import com.dugq.ThreadUtil;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by dugq on 2023/4/7.
 */
public class LockSupportParkTest {
    static Object obj_lock = new Object();
    static Lock lock = new ReentrantLock();
    static Condition condition = lock.newCondition();

    // lockSupport可以先释放再获取锁，获取锁时不会被阻塞
    @Test
    public void testParkAndUnParkFirst(){
        Thread t1 = new Thread(()->{
            System.out.println("thread started");
            ThreadUtil.sleep(3);
            System.out.println("thread sleep over and park thread");
            LockSupport.park();
            System.out.println("thread unParked");
        });
        t1.start();
        LockSupport.unpark(t1);
        System.out.println("unPark thread");
        ThreadUtil.sleep(30);
    }

    /**
     *  1、两个线程分别执行
     *    1.1、先对obj_lock对象进行加锁
     *    1.2、调用obj_lock的wait
     *    1.3、释放锁
     *  2、主线程等待两个线程都进入wait后
     *   2.1 先对obj_lock进行加锁
     *   2.2、在对obj_lock进行notifyAll
     *   2.3、释放锁
     *
     *  结果：
     * i got the lock first! dugq-wait-test1
     * i got the lock first! dugq-wait-test2
     * please check thread status！
     * main thread got the lock!
     * main thread release the lock !
     * please check thread status！
     * i got it lock second! dugq-wait-test2
     * release lock! dugq-wait-test2
     * please check thread status！
     * i got it lock second! dugq-wait-test1
     * release lock! dugq-wait-test1
     *
     */
    @Test
    public void testWaitAndNotify() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        CountDownLatch countDownLatch2 = new CountDownLatch(2);
        new Thread(() -> testWait(countDownLatch, countDownLatch2), "dugq-wait-test1").start();
        new Thread(() -> testWait(countDownLatch, countDownLatch2), "dugq-wait-test2").start();
        countDownLatch.await();
        //WAITING (on object monitor)
        System.out.println("please check thread status！");
        Thread.sleep(10000);
        synchronized (obj_lock){
            System.out.println("main thread got the lock! ");
            obj_lock.notifyAll();
        }
        System.out.println("main thread release the lock ! ");
        countDownLatch2.await();
    }

    public void testWait(CountDownLatch countDownLatch,CountDownLatch countDownLatch2){
        synchronized (obj_lock){
            System.out.println("i got the lock first! "+Thread.currentThread().getName());
            countDownLatch.countDown();
            try {
                obj_lock.wait();
                // 一个在TIMED_WAITING (sleeping) 另一个线程状态变更为： BLOCKED (on object monitor)
                System.out.println("please check thread status！");
                Thread.sleep(100000);
                System.out.println("i got it lock second! "+Thread.currentThread().getName());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("release lock! "+Thread.currentThread().getName());
        countDownLatch2.countDown();
    }


    /**
     * 测试：
     *  1、两个线程分别执行
     *    1.1、先对obj_lock对象进行加锁
     *    1.2、调用obj_lock的wait
     *    1.3、释放锁
     *  2、主线程等待两个线程都进入wait后
     *   2.1 先对obj_lock进行加锁
     *   2.2、在对obj_lock进行notifyAll
     *   2.3、释放锁
     *
     *  结果：
     * i got the lock ! before await!dugq-condition-wait-test1
     * i got the lock ! before await!dugq-condition-wait-test2
     * please check thread status！            -------- WAITING (parking)
     * before signalAll!
     * after signalAll!
     * i got the lock ! after wait!dugq-condition-wait-test1
     * please check thread status！
     * after release lock !dugq-condition-wait-test1
     * i got the lock ! after wait!dugq-condition-wait-test2
     * please check thread status！            ---------未获取到锁的依然是：WAITING (parking)
     * after release lock !dugq-condition-wait-test2
     *
     */

    @Test
    public void testConditionWaitAndNotify() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        CountDownLatch countDownLatch2 = new CountDownLatch(2);
        new Thread(()->testCondition(countDownLatch,countDownLatch2),"dugq-condition-wait-test1").start();
        new Thread(()->testCondition(countDownLatch,countDownLatch2),"dugq-condition-wait-test2").start();

        countDownLatch.await();
        //两线程都是 WAITING (parking)
        System.out.println("please check thread status！");
        Thread.sleep(20000);
        lock.lock();
        System.out.println("before signalAll!");
        condition.signalAll();
        System.out.println("after signalAll!");
        lock.unlock();
        countDownLatch2.await();
    }

    public void testCondition(CountDownLatch countDownLatch,CountDownLatch countDownLatch2){

        try {
            lock.lock();
            countDownLatch.countDown();
            System.out.println("i got the lock ! before wait!"+Thread.currentThread().getName());
            condition.await();
            System.out.println("i got the lock ! after wait!"+Thread.currentThread().getName());
            // 一个在TIMED_WAITING (sleeping) ，另一个  WAITING (parking)
            System.out.println("please check thread status！");
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            lock.unlock();
            System.out.println("after release lock !"+Thread.currentThread().getName());
            countDownLatch2.countDown();
        }
    }


    @Test
    public void testConditionFair() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(7);
        CountDownLatch countDownLatch2 = new CountDownLatch(7);
        new Thread(()->testCondition2(countDownLatch,countDownLatch2),"dugq-condition-wait-test1").start();
        new Thread(()->testCondition2(countDownLatch,countDownLatch2),"dugq-condition-wait-test2").start();
        new Thread(()->testCondition2(countDownLatch,countDownLatch2),"dugq-condition-wait-test3").start();
        new Thread(()->testCondition2(countDownLatch,countDownLatch2),"dugq-condition-wait-test4").start();
        new Thread(()->testCondition2(countDownLatch,countDownLatch2),"dugq-condition-wait-test5").start();
        new Thread(()->testCondition2(countDownLatch,countDownLatch2),"dugq-condition-wait-test6").start();
        new Thread(()->testCondition2(countDownLatch,countDownLatch2),"dugq-condition-wait-test7").start();
        countDownLatch.await();
        Thread.sleep(10000);
        lock.lock();
        condition.signalAll();
        Thread.sleep(10000);
        lock.unlock();
        countDownLatch2.await();
    }

    public void testCondition2(CountDownLatch countDownLatch,CountDownLatch countDownLatch2){

        try {
            lock.lock();
            countDownLatch.countDown();
            System.out.println("before wait!"+Thread.currentThread().getName());
            condition.await();
            System.out.println("after wait!"+Thread.currentThread().getName());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            lock.unlock();
            countDownLatch2.countDown();
        }
    }

}
