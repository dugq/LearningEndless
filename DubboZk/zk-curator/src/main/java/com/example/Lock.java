package com.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by dugq on 2023/9/4.
 */
@Slf4j
public class Lock {
    public static void main(String[] args) throws Exception {
        CuratorFramework curatorClient = ZkClientFactory.getCuratorClient();
        testMultiLock(curatorClient);
        //        testLockImpl(curatorClient);
//        testReEntrantLock(curatorClient);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await();
    }

    @Test
    public void test1() throws Exception {
        CuratorFramework curatorClient = ZkClientFactory.getCuratorClient();
        InterProcessMutex lock = new InterProcessMutex(curatorClient, "/dugq/locks/multiThread");
        lock.acquire(10,TimeUnit.MINUTES);
        System.out.println("i got it. test1");
        TimeUnit.SECONDS.sleep(10);
        curatorClient.close();
    }

    @Test
    public void test2() throws Exception {
        CuratorFramework curatorClient = ZkClientFactory.getCuratorClient();
        InterProcessMutex lock = new InterProcessMutex(curatorClient, "/dugq/locks/multiThread");
        lock.acquire(10,TimeUnit.MINUTES);
        System.out.println("i got it. test2");
        TimeUnit.MINUTES.sleep(10);
    }

    private static void testMultiLock(CuratorFramework curatorClient) throws InterruptedException {
        String path = "/dugq/locks/multi-";
        InterProcessMutex lock = new InterProcessMutex(curatorClient, path);
        for (int i = 0; i < 10; i++) {
            final int index = i;
            Thread thread = new Thread(() -> lock(lock, index));
            thread.setName("zk----test ---"+i);
            thread.start();
            Thread.sleep(100);
        }
    }
    private static void lock(InterProcessMutex lock, int index){
        try {
            lock.acquire();
            System.err.println("i got it ----------------"+index);
            Thread.sleep(20000);
            lock.release();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 锁实现流程：
     * <li>1、使用变量LockData存储当前线程信息以及锁次数
     * <li>2、如果 ConcurrentHashMap 中已存在当前线程，则取出lockData，将lockCount自增。       【实现了可重入】 问题：可重入依赖单个InterProcessMutex变量，跨变量就凉凉
     * <li>
     * <li>3、利用CuratorFramework客户端连接zk创建有序节点  : client.create().creatingParentsIfNeeded().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path, lockNodeBytes)
     * <li>4、
     * <li>7、将lockData 以Thread为key存入ConcurrentHashMap中
     */
    private static void testLockImpl(CuratorFramework curatorClient) throws Exception {
        String path = "/dugq/locks/fileType";
        InterProcessMutex lock = new InterProcessMutex(curatorClient, path);
        ZKOpTest.printChildren(curatorClient,path,"init");
        lock.acquire(3,TimeUnit.SECONDS);
        ZKOpTest.printChildren(curatorClient,path,"lock first");
        lock.acquire(3,TimeUnit.SECONDS);
        ZKOpTest.printChildren(curatorClient,path,"lock second");
        lock.release();
        ZKOpTest.printChildren(curatorClient,path,"release first ");
        lock.release();
        ZKOpTest.printChildren(curatorClient,path,"release second");
    }

    //可重入的基础是同一个lock，可重入是客户端本地实现的，而非服务端。
    public static void testReEntrantLock(CuratorFramework curatorClient) throws Exception {
        InterProcessMutex lock = new InterProcessMutex(curatorClient,"/dugq/locks/test");
        InterProcessMutex lock2 = new InterProcessMutex(curatorClient,"/dugq/locks/test");
        try{
            lock.acquire();
            Thread.sleep(1000);
            log.error("------------lock2 re-entrant result :{}",lock2.acquire(3, TimeUnit.SECONDS));
            log.error("------------lock1 re-entrant result {}",lock.acquire(3,TimeUnit.SECONDS));
            lock.release();
            lock.release();
        }catch (Exception e){
            log.error("lock error",e);
        }
    }
}
