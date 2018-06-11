package com.example;

import com.example.controller.login.Test123;
import com.example.intercepter.TestWebRequestInterceptor;
import com.example.intercepter.ValidatorInterception;
import com.example.pojo.mbeans.MBeanImpl.TestMBeanImpl;
import com.example.pojo.mbeans.TestMBean;
import com.example.pojo.statics.JMXProps;
import com.example.pojo.statics.StaticVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cglib.core.DebuggingClassWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.remoting.rmi.RmiRegistryFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.management.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;

@SpringBootApplication
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
//		List<String> optionValues = applicationArguments.getOptionValues("Dmysql.database");
        //获取参数名称
//		Set<String> optionNames = applicationArguments.getOptionNames();
        //获取所有的参数即：args
//		String[] sourceArgs = applicationArguments.getSourceArgs();

//		开启代理生成的类打印
//		System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "D:\\class");
//		System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        SpringApplication.run(DemoApplication.class, args);
        logger.error("CONGRATULATIONS!!   demo effective!");
        logger.error(StaticVar.myProperties);
        logger.error(StaticVar.url);


        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName mxbeanName = new ObjectName("com.example.pojo.mbeans.impl:type=TestMBeanImpl");
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

    @Bean
    public Object test(@Autowired StaticVar staticVar, @Autowired JMXProps jmxProps) {
        System.out.println(staticVar.getFlag() + "==============================");
        logger.error(jmxProps.getAuthenticate());
        logger.error(jmxProps.getPort());
        logger.error(jmxProps.getSsl());
        logger.error(jmxProps.getHostName());


        ClassPathResource resource = new ClassPathResource("generatorConfig.xml");
        return new Test123(1);
    }

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
