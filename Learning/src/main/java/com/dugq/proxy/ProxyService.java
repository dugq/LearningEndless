package com.dugq.proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public interface ProxyService {

    default void printClass(Class<?> clazz) {
        System.out.println("Class Name: " + clazz.getName());
        System.out.println("Methods:");
        for (Method method : clazz.getMethods()) {
            System.out.println("  " + method.getName());
        }
    }

    <T> Class<T> generatorClass(Class<T> interfaceClass);

    <T> T generatorProxy(Class<T> interfaceClass, InvocationHandler handler);
}
