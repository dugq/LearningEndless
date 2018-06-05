package com.example.pojo.statics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by dugq on 2017/10/30.
 */
@Component
@ConfigurationProperties
public class StaticVar {

    public static String url;

    public static String test;

    public static String test1;

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
}
