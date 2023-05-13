package com.example.learn.jreApi.guava;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.RateLimiter;
import com.google.common.util.concurrent.Uninterruptibles;

import java.util.concurrent.TimeUnit;

/**
 * @author dugq
 * @date 2020-07-09 11:53
 */
public class GuavaRateLimiterTest {
    public static void main(String[] args) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        RateLimiter rateLimiter = RateLimiter.create(10);
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        double acquire0 = rateLimiter.acquire(10);
        System.out.println("0="+acquire0);
        Uninterruptibles.sleepUninterruptibly(10, TimeUnit.MILLISECONDS);
        double acquire = rateLimiter.acquire(10);
        System.out.println("1="+acquire);
        double acquire1 = rateLimiter.acquire(1);
        System.out.println("2="+acquire1);
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println("total="+elapsed);
    }
}
