package com.example.pojo.mbeans;

import org.springframework.jmx.export.annotation.*;
import org.springframework.stereotype.Component;

import javax.management.ObjectName;

/**
 * Created by dugq on 2018\6\10 0010.
 * 1、不需要像JMX那样繁琐的定义，声明式注解标记，并且声明为Bean
 * 2、可定义操作 属性  以及通知
 * 3、远程  无需通过参数配置，需要定义RmiRegistryFactoryBean 和 ConnectorServerFactoryBean两个bean 并且后者依赖前者
 *
 *
 */
@ManagedResource(objectName="test:name=springMbean",description = "wwanwan")
@Component("springMXBean")
public class SpringMBean {
    private String name ;

    public String getName() {
        return name;
    }
    @ManagedAttribute(description="The Name Attribute")
    public void setName(String name) {
        this.name = name;
    }
    @ManagedOperation(description="Add two numbers")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "x", description = "The first number"),
            @ManagedOperationParameter(name = "y", description = "The second number")})
    public int add_1(int x, int y) {
        return x + y;
    }

    @ManagedOperation
    public int add_2(int x, int y){
        return x + y;
    }

    public void dontExposeMe() {
        throw new RuntimeException();
    }


}
