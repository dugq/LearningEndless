package com.example;

import com.example.intercepter.ValidatorInterception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Arrays;

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

	public static void main(String[] args) {
		//启动参数添加：--Dfile.encoding=UTF-8 --mysql.database=spring-boot --server.port=8090 --mycofig.database=spring_boot 跟yml同名的属性将覆盖
		ApplicationArguments applicationArguments = new DefaultApplicationArguments(
				args);
		logger.info("启动参数："+Arrays.toString(args));
		//获取启动参数
//		List<String> optionValues = applicationArguments.getOptionValues("Dmysql.database");
		//获取参数名称
//		Set<String> optionNames = applicationArguments.getOptionNames();
		//获取所有的参数即：args
//		String[] sourceArgs = applicationArguments.getSourceArgs();

		SpringApplication.run(DemoApplication.class, args);
		logger.error("CONGRATULATIONS!!   demo effective!");
    }
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/mystatic/**")
				.addResourceLocations("classpath:/static/");
		registry.addResourceHandler("/template/**")
				.addResourceLocations("classpath:/staticTemplate/");
	}



	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new ValidatorInterception()).addPathPatterns("/**");
		super.addInterceptors(registry);
	}
}
