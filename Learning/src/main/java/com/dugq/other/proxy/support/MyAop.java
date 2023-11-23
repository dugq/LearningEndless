package com.dugq.other.proxy.support;


import java.lang.reflect.Method;

/**
 * Created by dugq on 2018/4/26.
 */
public abstract class MyAop {
    public void before(MyAdvisorSupport support){}
    public Object around(MyAdvisorSupport support, Method method, Object[] args){
       return support.invoke(method,args);
    }

    public void after(MyAdvisorSupport support){}


}
