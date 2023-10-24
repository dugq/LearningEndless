package com.dugq.jreApi.lock;

import com.example.util.ThreadUtil;
import org.junit.Test;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * Created by dugq on 2023/10/13.
 */
public class SynTest {
    private ExecutorService executorService = ThreadUtil.getThreadPool("synchronizedTest");
    private static CountDownLatch countDownLatch;

    private String name = "default";

    private static void countDown() {
        if (Objects.nonNull(countDownLatch)){
            countDownLatch.countDown();
        }
    }
    public synchronized void test(){
        System.out.println("synchronized method 1 start!");
        ThreadUtil.sleep(10);
        System.out.println("synchronized method 1 end!");
        countDown();
    }


    public synchronized void test1(){
        System.out.println("synchronized method 2 start!");
        ThreadUtil.sleep(10);
        System.out.println("synchronized method 2 end!");
        countDown();
    }

    public void test2(){
        synchronized (SynTest.class){
            System.out.println("synchronized class 1 start!");
            ThreadUtil.sleep(10);
            System.out.println("synchronized class 1 end!");
            countDown();
        }
    }

    public void test3(){
        synchronized (SynTest.class){
            System.out.println("synchronized class 2 start!");
            ThreadUtil.sleep(10);
            System.out.println("synchronized class 2 end!");
            countDown();
        }
    }

    public void test4(){
        synchronized (this){
            System.out.println("synchronized object 1 start!");
            ThreadUtil.sleep(10);
            System.out.println("synchronized object 1 end!");
            countDown();
        }
    }

    public void test5(){
        synchronized (this){
            System.out.println("synchronized object 2 start!");
            ThreadUtil.sleep(10);
            System.out.println("synchronized object 2 end!");
            countDown();
        }
    }

    public static synchronized void test6(){
        System.out.println("synchronized static method 1 start!");
        ThreadUtil.sleep(10);
        System.out.println("synchronized static method 1 end!");
        countDown();
    }


    public static synchronized void test7(){
        System.out.println("synchronized static method 2 start!");
        ThreadUtil.sleep(10);
        System.out.println("synchronized static method 2 end!");
        countDown();
    }


    @Test
    public void testAll() throws InterruptedException {
        countDownLatch = new CountDownLatch(8);
        executorService.execute(this::test);
        executorService.execute(this::test1);
        executorService.execute(this::test2);
        executorService.execute(this::test3);
        executorService.execute(this::test4);
        executorService.execute(this::test5);
        executorService.execute(SynTest::test6);
        executorService.execute(SynTest::test7);
        countDownLatch.await();

        /**
         synchronized method 1 start!
         synchronized class 1 start!
         ---> method 和 class 不冲突
         ---> static 2个 和 object 2个 被阻塞
         synchronized method 1 end!
         synchronized class 1 end!
         synchronized object 1 start!
         synchronized class 2 start!
         ---> object 和 class 不冲突
         ---> static 2个 和 method 1个 被阻塞
         synchronized object 1 end!
         synchronized class 2 end!
         synchronized method 2 start!
         synchronized static method 2 start!
         ---> static 和 method 不冲突
         ---> object 1个 static 1个 被阻塞
         synchronized method 2 end!
         synchronized static method 2 end!
         synchronized object 2 start!
         synchronized static method 1 start!
         ---> static object 不冲突

         ======》method 和 object 冲突            ======》再确认一下不同对象的效果 {@link #testObj}
         ======》static method 和 class 冲突      static method 锁的是class 对象没跑了
         */
    }

    @Test
    public void testObj() throws InterruptedException {
        SynTest Obj1 = new SynTest();
        Obj1.name="1111";
        SynTest Obj2 = new SynTest();
        Obj2.name="2222";
        countDownLatch = new CountDownLatch(5);
        executorService.execute(Obj1::testMethod);
        executorService.execute(Obj2::testMethod);
        executorService.execute(Obj1::testMethod2);
        executorService.execute(Obj2::testMethod2);
        executorService.execute(this::test4);
        countDownLatch.await();
        /**
         synchronized method 1 start! name=1111
         synchronized method 1 start! name=2222
         synchronized object 1 start!
         ------> 三个线程分别锁的对象是： obj1 obj2 和 this，互不冲突
         ------> 阻塞了obj1 obj2 的 method 2
         synchronized method 1 end!name=1111
         synchronized method 1 end!name=2222
         synchronized method 2 start! name=1111
         synchronized method 2 start! name=2222
         synchronized object 1 end!
         ------> this 不影响 obj1 和 obj2 先拿到锁
         synchronized method 2 end!name=2222
         synchronized method 2 end!name=1111


         ======》method 和 object 冲突  ======》 method 锁的是调用method的对象
         */
    }

    public synchronized void testMethod(){
        System.out.println("synchronized method 1 start! name="+name);
        ThreadUtil.sleep(10);
        System.out.println("synchronized method 1 end!name="+name);
        countDown();
    }

    public synchronized void testMethod2(){
        System.out.println("synchronized method 2 start! name="+name);
        ThreadUtil.sleep(10);
        System.out.println("synchronized method 2 end!name="+name);
        countDown();
    }

}
