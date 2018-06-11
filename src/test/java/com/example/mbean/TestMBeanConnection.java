package com.example.mbean;


import org.springframework.jmx.support.MBeanServerConnectionFactoryBean;

import javax.management.MBeanServerConnection;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by dugq on 2018/6/11.
 */
public class TestMBeanConnection {
    public static void main(String[] args) throws IOException {
        MBeanServerConnectionFactoryBean factoryBean = new MBeanServerConnectionFactoryBean();
        factoryBean.setServiceUrl("service:jmx:rmi://192.168.1.216/jndi/rmi://192.168.1.216:8804/springMbean");
        MBeanServerConnection object = factoryBean.getObject();
        Integer mBeanCount = object.getMBeanCount();
        System.out.println(mBeanCount);
    }
}
