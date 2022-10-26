package com.example.demo.spring.pojo.statics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by dugq on 2017/10/30.
 *
 * 1、注入属性的三种方式：@Value 注解在非静态属性上
 * 2、@Value 写在非静态set方法上
 * 3、@ConfigurationProperties 指定前缀后，会自动为最后一个命名的属性赋值 eg：flag
 *
 *
 *
 */
@Component("staticVar")
@ConfigurationProperties(prefix = "myProperties")
public class StaticVar {
    private String flag;

    public static String url;

    public static String test;

    public static String test1;

    public static String myProperties;

    @Value("${myProperties.flag}")
    private String myPropertiesFlag;

    public  String getTest1() {
        return test1;
    }
    @Value("${spring.datasource.url1:123123123}")
    public  void setTest1(String test1) {
        StaticVar.test1 = test1;
    }

    public String getUrl() {
        return url;
    }
    @Value("${spring.datasource.url}")
    public void setUrl(String url) {
        StaticVar.url = url;
    }

    public  String getTest() {
        return test;
    }

    public void setTest(String test) {
        StaticVar.test = test;
    }

    public  String getMyProperties() {
        return myProperties;
    }
    @Value("${myProperties.flag}")
    public  void setMyProperties(String myProperties) {
        StaticVar.myProperties = myProperties;
    }

    public String getMyPropertiesFlag() {
        return myPropertiesFlag;
    }

    public void setMyPropertiesFlag(String myPropertiesFlag) {
        this.myPropertiesFlag = myPropertiesFlag;
    }


    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
