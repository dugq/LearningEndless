package com.example.pojo.mbeans;

import org.springframework.jmx.export.annotation.*;
import org.springframework.stereotype.Component;

import javax.management.ObjectName;

/**
 * Created by dugq on 2018\6\10 0010.
 */
@ManagedResource(objectName="springMbean",description = "wwanwan")
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
