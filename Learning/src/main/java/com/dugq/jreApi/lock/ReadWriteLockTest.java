package com.dugq.jreApi.lock;

import com.dugq.StringNumberUtil;
import com.dugq.ThreadUtil;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

/**
 * Created by dugq on 2023/4/7.
 */
public class ReadWriteLockTest {

    // 读写锁状态分开控制，由 state 的高16位计数读，低16位计数写
    // 读写锁使用threadLocal记录当前线程加锁次数，以及线程ID，在解锁时强制要求必须和加锁线程相同，否则报错
    // 先加写锁后，可再加读锁，且读写的解锁不要求顺序，但先加读锁，不能再加写锁
    @Test
    public void testReentrantWrite(){
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        // 先加读锁会导致writeLock卡死
        // readLock.lock();
        printAQSState(lock);
        System.out.println("--------------------------------lock write------------------------");
        writeLock.lock();
        printAQSState(lock);
        writeLock.lock();
        printAQSState(lock);
        writeLock.lock();
        printAQSState(lock);
        System.out.println("--------------------------------read write------------------------");
        readLock.lock();
        printAQSState(lock);
        readLock.lock();
        printAQSState(lock);
        readLock.lock();
        printAQSState(lock);
        System.out.println("--------------------------------unlock write------------------------");
        writeLock.unlock();
        printAQSState(lock);
        writeLock.unlock();
        printAQSState(lock);
        writeLock.unlock();
        printAQSState(lock);
        System.out.println("--------------------------------unlock read------------------------");
        readLock.unlock();
        printAQSState(lock);
        readLock.unlock();
        printAQSState(lock);
        readLock.unlock();
        printAQSState(lock);
        readLock.unlock();
        printAQSState(lock);
    }

    // 读锁，使用ThreadLocal记录了单个线程占用读锁次数
    // 同时，也占用了读锁的总次数
    @Test
    public void testReadReentrant(){
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        // 第一个获取读锁的线程被单独记录了，不使用threadLocal。这里为了跳过这一优化策略
        new Thread(()->readLock.lock()).start();
        ThreadUtil.sleep(1);
        printAQSState(lock);
        readLock.lock();
        printAQSState(lock);
        readLock.lock();
        printAQSState(lock);
        readLock.lock();
        printAQSState(lock);
        readLock.unlock();
        printAQSState(lock);
        readLock.unlock();
        printAQSState(lock);
        readLock.unlock();
        printAQSState(lock);
    }

    private static void printAQSState(ReentrantReadWriteLock lock){
        try {

            System.out.print("threadLock times read "+lock.getReadHoldCount()+" write "+lock.getWriteHoldCount());
            Field sync = lock.getClass().getDeclaredField("sync");
            sync.setAccessible(true);
            AbstractQueuedSynchronizer aqs = (AbstractQueuedSynchronizer)sync.get(lock);
            Field state = AbstractQueuedSynchronizer.class.getDeclaredField("state");
            state.setAccessible(true);
            int sta = (int)state.get(aqs);
            StringNumberUtil.printBinaryString(sta,true,true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private final StampedLock STAMPED_LOCK = new StampedLock();

    @Test
    public void testStampedLockAPI(){
        long opReadStamp = STAMPED_LOCK.tryOptimisticRead();
        printState();
        long readStamp = STAMPED_LOCK.readLock();
        printState();
        STAMPED_LOCK.unlockRead(readStamp);
        printState();
        long writeStamp = STAMPED_LOCK.writeLock();
        printState();
        STAMPED_LOCK.unlockWrite(writeStamp);
        printState();
        STAMPED_LOCK.validate(opReadStamp);
        // STAMPED_LOCK.asReadLock().unlock();
    }

    // 每次lock 和 unlock 都会在state的二进制基础上增加1<<7 位
    // 其中state的第7位表示写锁
    @Test
    public void testStampedLockWrite(){
        for (int i =0; i< 10; i++){
            long writeStamp = STAMPED_LOCK.writeLock();
            printState();
            STAMPED_LOCK.unlockWrite(writeStamp);
            printState();
        }
    }

    // lock的低7位表示读锁的个数
    @Test
    public void testStampedLockRead(){
        for (int i =0; i< 10; i++){
            STAMPED_LOCK.readLock();
            printState();
        }
        for (int i =0; i< 10; i++){
            STAMPED_LOCK.asReadLock().unlock();
            printState();
        }
    }

    Object wait_lock_1 = new Object();
    Object wait_lock_2 = new Object();
    // 写锁实现：没有读锁时，直接获取写锁，有读锁时，进入队列，后续的读锁直接进入队列排队。
    @Test
    public void testStampedLockWriteOnRead() throws InterruptedException {
        new Thread(()->run(STAMPED_LOCK.asReadLock(),wait_lock_1),"read_thread_1").start();
        new Thread(()->run(STAMPED_LOCK.asReadLock(),wait_lock_1),"read_thread_2").start();
        new Thread(()->run(STAMPED_LOCK.asWriteLock(), wait_lock_2),"write_thread_1").start();
        new Thread(()->run(STAMPED_LOCK.asReadLock(), null),"read_thread_3").start();
        ThreadUtil.sleep(1);
        ThreadUtil.threadNotify(wait_lock_1);
        ThreadUtil.sleep(3);
        ThreadUtil.threadNotify(wait_lock_2);
        new CountDownLatch(1).await();
    }

    // 读虽然可以重入，但消耗并发量，写是不可重入的
    @Test
    public void testStampedLockReentrant() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CountDownLatch countDownLatch2 = new CountDownLatch(1);
        new Thread(()->{
            long read1 = STAMPED_LOCK.readLock();
            printState();
            long read2 =  STAMPED_LOCK.readLock();
            printState();
            STAMPED_LOCK.unlockRead(read1);
            STAMPED_LOCK.unlockRead(read2);
            countDownLatch.countDown();
        }).start();

        new Thread(()->{
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            STAMPED_LOCK.writeLock();
            printState();
            // 被阻塞
            STAMPED_LOCK.writeLock();
            printState();
            countDownLatch2.countDown();
        }).start();
        countDownLatch2.await();
        System.out.println("over");
    }



    public void run(Lock lock, Object waitLock){
        try {
            lock.lock();
            System.out.println("get lock "+Thread.currentThread().getName());
            if (Objects.nonNull(waitLock)){
                ThreadUtil.threadWait(waitLock);
            }
            System.out.println("release lock "+Thread.currentThread().getName());
            lock.unlock();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


   public void printState(){
        try {
            Field state = STAMPED_LOCK.getClass().getDeclaredField("state");
            state.setAccessible(true);
            long s = (long)state.get(STAMPED_LOCK);
            StringNumberUtil.printBinaryString(s,true,true);
        }catch (Exception e){
            e.printStackTrace();
        }

   }

}
