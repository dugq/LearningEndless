package com.dugq.other.proxy.jdk;

import com.example.learn.other.proxy.Person;
import com.example.learn.other.proxy.support.MyAdvisorSupport;
import com.example.learn.other.proxy.support.MyAop;
import org.springframework.aop.framework.*;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.DecoratingProxy;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Created by dugq on 2018/4/26.
 */
public class MyJdkDynamicAopProxyFactory<T> implements AopProxy, InvocationHandler,Serializable {
    private static final long serialVersionUID = -6412792136324779022L;
    private final MyAdvisorSupport advised;

    private boolean equalsDefined;
    private boolean hashCodeDefined;
    private Object bean;

    public MyJdkDynamicAopProxyFactory(List<MyAop> aopList, Object bean) {
        this.advised = new MyAdvisorSupport(aopList,bean);
        this.bean = bean;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (!this.equalsDefined && AopUtils.isEqualsMethod(method)) {
                return equals(args[0]);
            }
            else if (!this.hashCodeDefined && AopUtils.isHashCodeMethod(method)) {
                return hashCode();
            }
            else if (method.getDeclaringClass() == DecoratingProxy.class) {
                return AopProxyUtils.ultimateTargetClass(this.advised);
            }
            return advised.invoke(method,args);
    }

    @Override
    public T getProxy() {
        return (T)Proxy.newProxyInstance(Person.class.getClassLoader(),
                new Class<?>[]{Person.class}, this);
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader,
                new Class<?>[]{Person.class}, this);
    }
}
