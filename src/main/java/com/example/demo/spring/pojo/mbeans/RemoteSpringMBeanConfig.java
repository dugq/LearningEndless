package com.example.demo.spring.pojo.mbeans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.remoting.rmi.RmiRegistryFactoryBean;

/**
 * Created by dugq on 2018\6\12 0012.
 */
//@Configuration
public class RemoteSpringMBeanConfig {

    //开启spring远程JMX
    @Bean
    public RmiRegistryFactoryBean rmiRegistryFactoryBean(){
        RmiRegistryFactoryBean rmiRegistryFactoryBean = new RmiRegistryFactoryBean();
        rmiRegistryFactoryBean.setPort(8804);
        return rmiRegistryFactoryBean;
    }
    @Bean
    @DependsOn("rmiRegistryFactoryBean")
    public ConnectorServerFactoryBean connectorServerFactoryBean(){
        ConnectorServerFactoryBean factoryBean = new ConnectorServerFactoryBean();
        factoryBean.setServiceUrl("service:jmx:rmi://192.168.1.216/jndi/rmi://192.168.1.216:8804/springMbean");
        return factoryBean;
    }

}
