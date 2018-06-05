package com.example.HeadFirst.adapterModel;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Created by dugq on 2018/5/14.
    适配器模式：将一个类的接口转化成客户期望的另一个接口。适配器让原来接口不兼容的类可以合作无间
    这里的例子：再老版本的jdk中，对于某些集合的迭代用的是enumeration，暴露出来的接口和现在流行的iterator是不兼容的，所以这里适用适配器模式，将接口转化为用户熟知的接口。
    现实例子：欧洲的插座（平行）遇上中国的插头（三角），这时候一种设计就是给插头装一个适配器，让他去适应欧洲的插座
 */
public class MyAdapter implements Iterator {
    Enumeration enumeration;

    public MyAdapter(Enumeration enumeration) {
        this.enumeration = enumeration;
    }

    @Override
    public boolean hasNext() {
        return enumeration.hasMoreElements();
    }

    @Override
    public Object next() {
        return enumeration.nextElement();
    }

    public static void main(String[] args) {
        ArrayList list = new ArrayList();
        System.out.println(list.iterator().hasNext());;
    }
}
