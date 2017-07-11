package com.example;

import com.example.BasicComponent.BasicComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@SpringBootApplication
@EnableTransactionManagement
public class DemoApplication extends BasicComponent{

    /**
     * 自己选择事务实现
     * @param dataSource
     * @return
     */
    @Bean("txManage")
	public PlatformTransactionManager txManager(DataSource dataSource){
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean
	public Object testTx(PlatformTransactionManager platformTransactionManager){
        logger.info("========TX_MANAGER==========================="+platformTransactionManager.getClass().getName());
		return null;
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		logger.error("CONGRATULATIONS!!   demo effective!");
    }
}
