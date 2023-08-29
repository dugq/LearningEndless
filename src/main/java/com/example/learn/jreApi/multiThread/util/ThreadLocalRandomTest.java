package com.example.learn.jreApi.multiThread.util;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by dugq on 2023/4/14.
 */
public class ThreadLocalRandomTest {

    @Test
    public void test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<ThreadLocalRandom> threadLocalRandomClass = ThreadLocalRandom.class;
        Method getProbe = threadLocalRandomClass.getDeclaredMethod("getProbe");
        getProbe.setAccessible(true);
        Object invoke = getProbe.invoke(null);
        System.out.println("default value = "+invoke);
        Thread thread = Thread.currentThread();
        ThreadLocalRandom.current();
        Object invoke2 = getProbe.invoke(null);
        Thread thread2 = Thread.currentThread();
        System.out.println("init value = "+invoke2);
    }

}
