package com.example.util;


import java.io.InputStream;
import java.net.URL;

/**
 * Created by dugq on 2018/4/16.
 */
public class GeneratorJSONObject {

    public static void generator(Class clazz){
        InputStream resourceAsStream = clazz.getClassLoader().getResourceAsStream(clazz.getName());
        System.out.println(resourceAsStream);
    }

    public static void main(String[] args) {

    }

}
