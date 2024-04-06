package com.dugq.frameworkDesign.分布式RPC框架;

import com.alibaba.fastjson2.JSON;
import com.dugq.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Created by dugq on 2023/8/28.
 */
@Slf4j
public class SerializeAndDeserialize {

    public User getUser(){
        User user = new User();
        user.setName("张三");
        user.setPwd("q23e123213");
        user.setId(123L);
        return user;
    }

    @Test
    public void testProtostuff(){
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



}
