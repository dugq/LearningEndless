package com.example.learn.other.proxy.cglib;

import com.example.learn.other.proxy.support.MyAdvisorSupport;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by dugq on 2018/4/27.
 */
@Deprecated
public class MyMethodInterceptor implements MethodInterceptor {
    MyAdvisorSupport myAdvisorSupport;

    public MyMethodInterceptor(MyAdvisorSupport myAdvisorSupport) {
        this.myAdvisorSupport = myAdvisorSupport;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return myAdvisorSupport.invoke(method,objects);
    }
}
