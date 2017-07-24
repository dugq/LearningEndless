package com.example;

import java.lang.reflect.Method;

/**
 * Created by dugq on 2017/6/22.
 */
public enum Six {
    man(1), woman(0);

    private int value;

    Six(int value) {
        this.value = value;
    }

    public int getValue()
    {
        return this.value;
    }

    public String getDes(){
        return this.value == 0 ? "女" : "男";
    }

    @Override
    public String toString() {
        return getDes();
    }


    public static void main(String[] args) {
        Method[] methods = Six.class.getMethods();
        for(Method method : methods){
            System.out.println(method.getName()+"=="+method.getReturnType().equals(String.class));
        }
        Object a = 1;
        System.out.print(a.getClass().isPrimitive());
    }

}

