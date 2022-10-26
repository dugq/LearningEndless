package com.example.learn.other.proxy.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dugq on 2018/4/26.
 */
public class MyAdvisorSupport {

     List<MyAop> interfaces = new ArrayList<>();
     public Object bean;
    int pos = 0;

    public MyAdvisorSupport(List<MyAop> interfaces, Object bean) {
        this.interfaces = interfaces;
        this.bean = bean;
    }

    public Object invoke(Method method, Object[] args){
        if(pos>=interfaces.size()){
            try {
                return method.invoke(bean,args);
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
        }
        MyAop aop = interfaces.get(pos++);
        aop.before(this);
        Object result = aop.around(this,method,args);
        aop.after(this);
        return result;
    }


}
