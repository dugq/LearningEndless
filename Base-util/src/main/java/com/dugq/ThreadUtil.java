package com.dugq;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadUtil {

    public static void sleep(int seconds){
        try {
            Thread.sleep(seconds* 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void interrupt(String msg){
        if (Thread.interrupted()){
            System.out.println("interrupt = "+msg);
            throw new RuntimeException(msg);
        }
    }

    private static final Map<String,ExecutorService> threadPoolMap = new ConcurrentHashMap<>();

    public static ExecutorService getThreadPool(String key) {
        if (threadPoolMap.containsKey(key)){
            return threadPoolMap.get(key);
        }
        return threadPoolMap.computeIfAbsent(key,(sufferName)-> new ThreadPoolExecutor(5, 10, 10, TimeUnit.MINUTES, new ArrayBlockingQueue<>(100), new ThreadFactory() {
            private final AtomicInteger index = new AtomicInteger(1);
            @Override
            public Thread newThread( Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("dugq-custom-"+sufferName+index.getAndAdd(1));
                return thread;
            }
        }));
    }

    public static void printThreadInfo(String flag){
        if (StringUtils.isBlank(flag)){
            flag = "current";
        }
        System.out.println(flag+" thread id = "+Thread.currentThread().getId()+" name = "+Thread.currentThread().getName());
    }

    public static void printThreadInfo(){
        printThreadInfo(null);
    }

    public static void threadWait(final Object lock){
        if (Objects.isNull(lock)){
            return;
        }
        synchronized (lock){
            try {
                lock.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void threadNotify(Object lock){
        if (Objects.isNull(lock)){
            return;
        }
        synchronized (lock){
            lock.notifyAll();
        }
    }
}
