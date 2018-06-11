package com.example.pojo.remoteJMX;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jmx.support.MBeanServerConnectionFactoryBean;

import javax.management.MBeanServerConnection;
import java.net.MalformedURLException;

/**
 * Created by dugq on 2018/6/11.
 *  建立子项目以后再验证
 *
 *
 */
@Configuration
public class RemoteJMX {
    @Bean
    @Lazy
    public MBeanServerConnectionFactoryBean mBeanServerConnectionFactoryBean() throws MalformedURLException {
        MBeanServerConnectionFactoryBean factoryBean = new MBeanServerConnectionFactoryBean();
        factoryBean.setServiceUrl("service:jmx:rmi://192.168.1.216/jndi/rmi://192.168.1.216:8804/springMbean");
        return factoryBean;
    }

    @Bean
    @Lazy
    public Object test(MBeanServerConnection connection){
        System.out.println(connection);
        return new Object();
    }
}
