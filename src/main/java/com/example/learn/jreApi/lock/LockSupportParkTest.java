package com.example.learn.jreApi.lock;

import com.example.util.ThreadUtil;
import org.junit.Test;

import java.util.concurrent.locks.LockSupport;

/**
 * Created by dugq on 2023/4/7.
 */
public class LockSupportParkTest {

    // lockSupport可以先释放再获取锁，获取锁时不会被阻塞
    @Test
    public void testUnParkFirst(){
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

}
