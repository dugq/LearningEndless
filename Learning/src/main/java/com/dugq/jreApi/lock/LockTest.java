package com.dugq.jreApi.lock;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by dugq on 2023/11/21.
 */
public class LockTest {

    public void testLock(){
        ReentrantLock lock = new ReentrantLock();
        ReentrantReadWriteLock readwriteLock;
        lock.newCondition();

    }

}
