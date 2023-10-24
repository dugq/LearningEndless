package com.example.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

/**
 * Created by dugq on 2023/10/13.
 */
@SpringBootTest
public class TemplateTest {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Test
    public void test(){
        ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set("template","test");
        redisTemplate.expire("template",1, TimeUnit.MINUTES);
        System.out.println(valueOperations.get("template"));
    }
}
