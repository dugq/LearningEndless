package com.dugq.jreApi.multiThread;

import com.dugq.ThreadUtil;
import lombok.SneakyThrows;
import org.junit.Test;
import sun.jvm.hotspot.debugger.ThreadAccess;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by dugq on 2023/4/6.
 */
public class ThreadStatusTest {
    static volatile boolean flag = false;
    static int i =0;

    static final Object park = new Object();

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


    /**
     * 无论wait() park 还是sleep都是不占用CPU资源的
     * @throws InterruptedException
     */
    @Test
    public void testWaitAndNotify() throws InterruptedException {
        new Thread(()->{
            synchronized (ThreadStatusTest.park){
                try {
                    ThreadStatusTest.park.wait();
                    while (true){

                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        },"dugq-wait-test").start();

        new Thread(()->{
            try {
                Thread.sleep(100000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },"dugq-sleep-test").start();
        Thread.sleep(25000);
        synchronized (ThreadStatusTest.park){
            ThreadStatusTest.park.notify();
        }
        LockSupport.park();
    }


    @Test
    public void testStop(){
        Thread thread = new Thread(() -> {
            synchronized (ThreadStatusTest.park) {
                try {
                    ThreadStatusTest.park.wait();
                    while (true) {

                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "dugq-wait-test");
        System.out.println("before start state = "+thread.getState());
        thread.start();
        ThreadUtil.sleep(1);
        System.out.println("after start state = "+thread.getState());
        thread.stop();
        ThreadUtil.sleep(1);
        System.out.println("after stop state = "+thread.getState());
        thread.suspend();
        ThreadUtil.sleep(1);
        System.out.println("after suspend state = "+thread.getState());
        LockSupport.park();
    }

    @Test
    public  void testInterruptTimeWaitThread() throws InterruptedException{
        Thread thread = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("thread interrupted already");
                    return;
                } else {
                    long start = System.currentTimeMillis();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        System.out.println("interrupted sleep time = "+(System.currentTimeMillis()-start));
                    }
                }
            }
        });
        thread.start();
        Thread.sleep(1000);
        thread.interrupt();
        Thread.sleep(1000);
    }

    private final Object blocker = new Object();;
    @Test
    public  void testInterruptWait() throws InterruptedException{
        Thread thread = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("thread interrupted already");
                    return;
                } else {
                    long start = System.currentTimeMillis();
                    synchronized (blocker){
                        try {
                            blocker.wait();
                        } catch (InterruptedException e) {
                            System.out.println("interrupted wait thread time = "+(System.currentTimeMillis()-start));
                        }
                    }
                }
            }
        });
        thread.start();
        Thread.sleep(1000);
        thread.interrupt();
        Thread.sleep(1000);
    }

    /**
     * blocking状态的线程貌似并不能被中断
     */
    @Test
    public  void testInterruptSynchronized() throws InterruptedException{
        Thread thread = new Thread(() -> {
            System.out.println("thread started");
            synchronized (blocker){
                System.out.println("got lock!");
            }
            System.out.println("release lock!");
        });
        synchronized (blocker){
            thread.start();
            Thread.sleep(1000);
            thread.interrupt();
            Thread.sleep(1000);
            System.out.println("release lock");
        }
    }


    /**
     * 可以 Lock.lockInterruptibly内部是使用LockSupport.park 进行阻塞线程的，所以它是支持中断等待的
     * 但是Lock.lock内部会忽略这个状态，重新进行一次锁竞争，如果未争抢到，会再次进入wait状态
     * @throws InterruptedException
     */
    @Test
    public void testLock() throws InterruptedException {
        Lock lock = new ReentrantLock();
        lock.lock();
        Thread thread = new Thread(() -> {
            System.out.println("thread started");
//            lock.lock();
            try {
                lock.lockInterruptibly();
                System.out.println("got lock");
            } catch (InterruptedException e) {
                System.out.println("thread interrupt");
            }


        });

        thread.start();
        Thread.sleep(1000);
        thread.interrupt();
        Thread.sleep(1000);
        System.out.println("release lock");
        lock.unlock();
    }


}
