package com.dugq.jdbc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Created by dugq on 2024/1/16.
 */
@Configuration
@ConfigurationProperties
@Setter
@Getter
public class Props {

    @Value("${mysql.driver}")
    private String mysqlDriver;

    // 装载 jdbc driver
    @PostConstruct
    public void init() throws ClassNotFoundException {
        if (mysqlDriver!=null){
            Class.forName(mysqlDriver);
        }
    }

}
