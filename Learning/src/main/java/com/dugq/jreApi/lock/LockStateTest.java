package com.dugq.jreApi.lock;

import org.junit.Test;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by dugq on 2023/12/1.
 */
public class LockStateTest {
    static final Object park = new Object();

    static ReentrantLock lock = new ReentrantLock();
    @Test
    public void testLockState(){
        testSynchronizedState();
        synchronized (park){
            testSynchronizedState();
        }
        testSynchronizedState();
        lock.lock();
        testJUCLockState();
        lock.unlock();
        testJUCLockState();
    }

    public void testSynchronizedState(){
        boolean value = Thread.holdsLock(park);
        System.out.println("thread hold the lock : "+value);
        if (!value){
            // do something
        }
        // do something
    }

    public void testJUCLockState(){
        boolean value = lock.isHeldByCurrentThread();
        System.out.println("thread hold the juc lock : "+value);
        if (!value){
            // do something
        }
        // do something
    }
}
