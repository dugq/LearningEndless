package com.dugq.other.proxy.test;


import com.example.learn.other.proxy.Person;
import com.example.learn.other.proxy.Xiaoming;
import com.example.learn.other.proxy.jdk.MyJdkDynamicAopProxyFactory;
import com.example.learn.other.proxy.support.MyAdvisorSupport;
import com.example.learn.other.proxy.support.MyAop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dugq on 2018/4/26.
 */
public class TestJavaProxy {

    public static void main(String[] args) {
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        MyAop aop1 = new MyAop() {
            @Override
            public void before(MyAdvisorSupport support) {
                System.out.println("i am 傻子");
            }
        };
        MyAop aop2 = new MyAop() {
            @Override
            public Object around(MyAdvisorSupport support, Method method, Object[] args) {
                System.out.println("around before");
                Object around = super.around(support, method, args);
                System.out.println("around after");
                return around;
            }
        };

        MyAop aop3 = new MyAop() {
            @Override
            public void after(MyAdvisorSupport support) {
                System.out.println("嗯哼~~~ ");
            }
        };

        List list = new ArrayList();
        list.add(aop1);
        list.add(aop2);
        list.add(aop3);
        Person xiaoming = new Xiaoming();
        xiaoming =new MyJdkDynamicAopProxyFactory<Person>(list,xiaoming).getProxy();
        xiaoming.say();
    }
}
