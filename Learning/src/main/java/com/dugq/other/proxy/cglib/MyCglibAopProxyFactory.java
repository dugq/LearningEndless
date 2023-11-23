package com.dugq.other.proxy.cglib;

import com.dugq.other.proxy.support.MyAdvisorSupport;
import com.dugq.other.proxy.support.MyAop;
import org.springframework.aop.framework.AopProxy;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by dugq on 2018/4/26.
 */
public class MyCglibAopProxyFactory<T> implements AopProxy,MethodInterceptor, Serializable {
    private static final long serialVersionUID = 8133634556078199239L;
    MyAdvisorSupport myAdvisorSupport;

    public MyCglibAopProxyFactory(List<MyAop> aopList, Object bean) {
        this.myAdvisorSupport =  new MyAdvisorSupport(aopList,bean);
    }

    @Override
    public T getProxy() {
        return getProxy(null);
    }

    @Override
    public T getProxy(ClassLoader classLoader) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(myAdvisorSupport.bean.getClass());
        enhancer.setCallback(this);
        Object enhanced;
        enhanced = enhancer.create();
        return (T)enhanced;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return myAdvisorSupport.invoke(method,objects);
    }
}
