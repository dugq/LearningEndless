package com.example.learn.frameworkDesign.分布式RPC框架;

import com.alibaba.fastjson2.JSON;
import com.example.demo.spring.pojo.entry.User;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sun.tools.internal.xjc.SchemaCache;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.K;
import org.junit.Test;
import org.springframework.cache.caffeine.CaffeineCache;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by dugq on 2023/8/28.
 */
@Slf4j
public class SerializeAndDeserialize {

    public User getUser(){
        User user = new User();
        user.setUsername("张三");
        user.setPassword("q23e123213");
        user.setUid(123);
        return user;
    }

    @Test
    public void testProtostuff(){
        printTime(()->ProtobufIOUtil.toByteArray(getUser(), getSchema(User.class,true), LinkedBuffer.allocate()),"protostuff serialize ");
        byte[] byteArray = ProtobufIOUtil.toByteArray(getUser(), getSchema(User.class, true), LinkedBuffer.allocate());
        printTime(()->ProtobufIOUtil.mergeFrom(byteArray,new User(),getSchema(User.class,true)),"protostuff deserialize ");
    }

    public void printTime(Runnable runnable,String flag){
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            runnable.run();
        }
        log.info("{} cost time = {} ms",flag,System.currentTimeMillis()-start);
    }

    @Test
    public void testJSON(){
        printTime(()->JSON.toJSONString(getUser()),"json ");
    }

    private static final Cache<Class<?>, Schema<?>> workCahce = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.SECONDS).maximumSize(1000).build();
    public <T> Schema<T> getSchema(Class<T> clazz, boolean useCache){
        if (!useCache){
            return RuntimeSchema.getSchema(clazz);
        }
        try {
           return (Schema<T>) workCahce.get(clazz,()-> RuntimeSchema.getSchema(clazz));
        } catch (ExecutionException e) {
            return RuntimeSchema.getSchema(clazz);
        }

    }


}
