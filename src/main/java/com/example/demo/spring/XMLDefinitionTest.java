package com.example.demo.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Locale;

/**
 * Created by dugq on 2018/5/4.
 */
public class XMLDefinitionTest {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("config/applicationContext.xml.back");
        applicationContext.getMessage("",new String[]{"zhangsan"}, Locale.CHINA);



    }
}
