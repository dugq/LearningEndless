package com.example.demo.spring.util;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.core.DefaultParameterNameDiscoverer;

import java.lang.reflect.Constructor;

/**
 * Created by dugq on 2018/5/10.
 */
public class getParamerName {
    public void test(){



        DefaultParameterNameDiscoverer defaultParameterNameDiscoverer = new DefaultParameterNameDiscoverer();
        Constructor<?>[] constructors = SqlSessionTemplate.class.getConstructors();
        for (Constructor constructor : constructors){
            String[] parameterNames = defaultParameterNameDiscoverer.getParameterNames(constructor);
            System.out.println(StringUtils.join(parameterNames,","));
        }
    }
}
