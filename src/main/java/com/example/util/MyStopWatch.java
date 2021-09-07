package com.example.util;

import lombok.Data;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author dugq
 * @date 2021/9/6 3:03 下午
 */
public class MyStopWatch {

    /**
     * 统计-总次数
     */
    private AtomicInteger times = new AtomicInteger(0);

    /**
     * 统计-总耗时
     */
    private AtomicLong totalTime = new AtomicLong(0);

    /**
     * 监控 线程监控
     */
    private ThreadLocal<MyWatch> myWatch = new ThreadLocal<>();

    public MyStopWatch() {
        reset();
    }

    @Data
    private class MyWatch{
        //是否正在计时
        private boolean watching;
        //最近一次监控的耗时
        private long lastTime;
        //最近一次开始时间
        private long lastStartTime;

        public void start(){
            if (watching){
                throw new RuntimeException("stop watch is start already!");
            }
            watching = true;

        }
        public long stop(){
            if (!watching){
                throw new RuntimeException("stop watch is not started!");
            }
            watching=false;
            return System.nanoTime()-lastStartTime;
        }
    }

    /**
     * 开始耗时监控
     */
    public void start(){
        this.myWatch.get().start();
    }

    /**
     * 结束并统计耗时
     * @return 本次监控耗时
     */
    public long stop(){
        times.incrementAndGet();
        final long thisTime = myWatch.get().stop();
        totalTime.addAndGet(thisTime);
        destroy();
        return thisTime;
    }

    /**
     * 重置监控，无关统计
     */
    public void reset() {
        myWatch.set(new MyWatch());
    }

    /**
     * 重置统计和监控
     */
    public void clear(){
        reset();
        totalTime = new AtomicLong();
        times = new AtomicInteger();
    }

    /**
     * 销毁监控
     */
    public void destroy(){
        myWatch.remove();
    }

}
