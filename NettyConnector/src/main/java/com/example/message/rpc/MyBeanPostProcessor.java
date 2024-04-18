package com.example.message.rpc;

import com.example.message.MessageApi;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by dugq on 2024/4/17.
 */
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Resource
    private MessageApi messageApi;
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof NettyClient){
            messageApi.setNettyClient((NettyClient) bean);
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
