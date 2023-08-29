package com.example.demo.spring;

import com.example.demo.spring.intercepter.TestWebRequestInterceptor;
import com.example.demo.spring.intercepter.ValidatorInterception;
import com.example.demo.spring.pojo.mbeans.MBeanImpl.TestMBeanImpl;
import com.example.demo.spring.pojo.statics.StaticVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cglib.core.DebuggingClassWriter;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.management.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableTransactionManagement
@ServletComponentScan
@EnableMBeanExport
public class DemoApplication extends WebMvcConfigurerAdapter {
    protected static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);
//	@Override
//	public void addCorsMappings(CorsRegistry registry) {
//		registry.addMapping("/**").allowedOrigins("*").allowCredentials(true).all
//				.allowedMethods("GET", "POST", "DELETE", "PUT","OPTIONS").maxAge(3600);
//	}

    public static void main(String[] args) throws IOException, MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
        //启动参数添加：--Dfile.encoding=UTF-8 --mysql.database=spring-boot --server.port=8090 --mycofig.database=spring_boot 跟yml同名的属性将覆盖
        ApplicationArguments applicationArguments = new DefaultApplicationArguments(
                args);
        logger.info("启动参数：" + Arrays.toString(args));
        //获取启动参数
		List<String> optionValues = applicationArguments.getOptionValues("Dmysql.database");
        //获取参数名称
		Set<String> optionNames = applicationArguments.getOptionNames();
        //获取所有的参数即：args
//		String[] sourceArgs = applicationArguments.getSourceArgs();

//		开启代理生成的类打印
		System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "D:\\class");
//		System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        SpringApplication.run(DemoApplication.class, args);

        new SpringApplication(DemoApplication.class).run(args);

        logger.error("CONGRATULATIONS!!   demo effective!");
        logger.error(StaticVar.myProperties);
        logger.error(StaticVar.url);


        configJMX();
    }

    private static void configJMX() throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName mxbeanName = new ObjectName("com.example.demo.spring.pojo.mbeans.impl:type=TestMBeanImpl");
        TestMBeanImpl mxbean = new TestMBeanImpl();
        mbs.registerMBean(mxbean, mxbeanName);
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
        registry.addWebRequestInterceptor(new TestWebRequestInterceptor()).addPathPatterns("/**");
        super.addInterceptors(registry);
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        System.out.println();
    }


}
