package com.dugq.proxy;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ProxyTest {

    public static void main(String[] args) {
        CglibProxy cglibProxy = new CglibProxy();
        JDKProxy jdkProxy = new JDKProxy();
        cglibProxy.generatorProxy(TestInterface.class,new MyInvocationHandler()).findById(1L);
        jdkProxy.generatorProxy(TestInterface.class,new MyInvocationHandler()).findById(2L);
    }

    @Slf4j
    static class MyInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
             log.info("method: {}, args: {}", method.getName(), args);
            return Mono.empty();
        }
    }

}
