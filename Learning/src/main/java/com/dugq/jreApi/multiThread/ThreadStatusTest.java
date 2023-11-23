package com.dugq.jreApi.multiThread;

import com.dugq.ThreadUtil;
import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by dugq on 2023/4/6.
 */
public class ThreadStatusTest {
    static volatile boolean flag = false;
    static int i =0;

    static Object park = new Object();

    static ReentrantLock lock = new ReentrantLock();

    static Condition condition = lock.newCondition();


    // 初始化状态和stop状态的线程 使用jconsole和jstack都是看不到的
    @SneakyThrows
    public static void main(String[] args) {

       // join 来测试Object的wait(time)方法                      WAITING (on object monitor)
        objectWaitTest();

        // join 来测试Object的wait方法                           TIMED_WAITING (on object monitor)
        objectWaitTimeOutTest();

        // sleep 方法                                           TIMED_WAITING (sleeping)
        threadSleepTest();

        // synchronized 测试，t1应该卡死                          BLOCKED (on object monitor)
        Thread t1 = synchronizedTest();

        //LockSupport-park测试，                                  WAITING (parking)
        unsafeParkTest();

        //LockSupport-park测试，                                  TIMED_WAITING (parking)
        parkUntilTest();

        // condition                                           WAITING (parking)
        conditionTest();

        lock.lock();
        // lock test                                            WAITING (parking)
        lockTest();
        //                                                      TIMED_WAITING (parking)
        lockTimeOutTest();


        synchronized (ThreadStatusTest.class){
            t1.start();
            while (!flag){
                Thread.sleep(10);
            }
            System.out.println("test over");

        }


    }

    private static void lockTimeOutTest() {
        new Thread(()-> {
            try {
                lock.tryLock(100, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },"dugq-lock-timeout-test").start();
    }

    private static void lockTest() {
        new Thread(()-> lock.lock(),"dugq-lock-test").start();
    }

    private static void conditionTest() {
        new Thread(()-> {
            lock.lock();
            try {
                condition.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },"dugq-condition-test").start();
    }

    private static void parkUntilTest() {
        new Thread(()->{LockSupport.parkUntil(park,System.currentTimeMillis()+100000);
            System.out.println("out");},"dugq-park-time-test").start();
    }

    private static void unsafeParkTest() {
        new Thread(LockSupport::park,"dugq-park-test").start();
    }

    private static Thread synchronizedTest() {
        Thread t1 = new Thread(()->{synchronized (ThreadStatusTest.class){
            System.out.println(1);
        }},"dugq-synchronized-blocking-test");
        return t1;
    }

    private static void threadSleepTest() {
        new Thread(()-> ThreadUtil.sleep(100),"dugq-thread-sleep-test").start();
    }

    private static void objectWaitTimeOutTest() {
        new Thread(() -> {
            try {
                Thread.currentThread().join();
                flag = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "dugq-Object-wait-thread").start();
    }

    private static void objectWaitTest() {
        new Thread(() -> {
            try {
                Thread.currentThread().join(100000);
                flag = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "dugq-Object-wait-time-thread").start();
    }

}
