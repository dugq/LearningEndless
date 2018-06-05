package com.example;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;

import java.lang.reflect.Constructor;
import java.util.Locale;

/**
 * Created by dugq on 2018/5/4.
 */
public class XMLDefinitionTest {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("config/applicationContext.xml");
        applicationContext.getMessage("",new String[]{"zhangsan"}, Locale.CHINA);



    }
}
