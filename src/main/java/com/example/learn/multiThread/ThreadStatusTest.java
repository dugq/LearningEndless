package com.example.learn.multiThread;

import com.example.util.ThreadUtil;
import lombok.SneakyThrows;

/**
 * Created by dugq on 2023/4/6.
 */
public class ThreadStatusTest {
    static volatile boolean flag = true;
    static int i =0;

    // 初始化状态和stop状态的线程 使用jconsole和jstack都是看不到的
    @SneakyThrows
    public static void main(String[] args) {
        new Thread(()->{},"dugq-T1");
        Thread thread1 = new Thread(() -> {
            try {
                Thread.currentThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "dugq-wating");
        thread1.start();
        new Thread(()-> ThreadUtil.sleep(1000),"dugq-time-wating").start();
        Thread t1 = new Thread(()->{synchronized (ThreadStatusTest.class){}},"dugq-blocking");
        new Thread(()->{synchronized (ThreadStatusTest.class){t1.start();while(flag)i++;}},"dugq-running").start();
        Thread thread = new Thread(() -> {
        }, "dugq-stop");
        thread.start();

    }

}
