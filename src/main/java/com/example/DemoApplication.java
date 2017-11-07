package com.example;

import com.example.BasicComponent.BasicComponent;
import com.example.intercepter.ValidatorInterception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.sql.DataSource;

@SpringBootApplication
@EnableTransactionManagement
@ServletComponentScan
public class DemoApplication extends WebMvcConfigurerAdapter {
	protected static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);
//	@Override
//	public void addCorsMappings(CorsRegistry registry) {
//		registry.addMapping("/**").allowedOrigins("*").allowCredentials(true).all
//				.allowedMethods("GET", "POST", "DELETE", "PUT","OPTIONS").maxAge(3600);
//	}

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
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/mystatic/**")
				.addResourceLocations("classpath:/statics/");
		registry.addResourceHandler("/template/**")
				.addResourceLocations("classpath:/staticTemplate/");
	}



	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new ValidatorInterception()).addPathPatterns("/**");
		super.addInterceptors(registry);
	}
}
