package com.example.util;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadUtil {

    public static void sleep(int seconds){
        try {
            Thread.sleep(seconds* 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
            public Thread newThread(@NotNull Runnable r) {
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
}
