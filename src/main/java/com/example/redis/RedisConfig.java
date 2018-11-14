package com.example.redis;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisShardInfo;

import java.nio.charset.Charset;

/**
 * Created by dugq on 2018/10/24 0024.
 */
public class RedisConfig {
    static RedisTemplate jdkTemplate;
    static  RedisTemplate jackTemplate;
    static  RedisTemplate jackson2Template;
    static StringRedisTemplate stringRedisTemplate;
    static MyStringRedisTemplate myStringRedisTemplate;
    static HessianTemplate hessianTemplate;

    static void init(){
        JedisShardInfo jedisShardInfo =  new JedisShardInfo("http://47.254.27.100:6379");
        jedisShardInfo.setPassword("1234");
        RedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory(jedisShardInfo);
        jdkTemplate = new RedisTemplate();
        jdkTemplate.setConnectionFactory(redisConnectionFactory);
        jdkTemplate.afterPropertiesSet();

        jackTemplate = new RedisTemplate();
        jackTemplate.setConnectionFactory(redisConnectionFactory);
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        jackTemplate.setDefaultSerializer(jsonSerializer);
        jackTemplate.afterPropertiesSet();

        jackson2Template = new RedisTemplate();
        jackson2Template.setConnectionFactory(redisConnectionFactory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        jackson2Template.setDefaultSerializer(jackson2JsonRedisSerializer);
        jackson2Template.afterPropertiesSet();

        stringRedisTemplate = new StringRedisTemplate(redisConnectionFactory);

        myStringRedisTemplate = new MyStringRedisTemplate(redisConnectionFactory);

        hessianTemplate = new HessianTemplate(redisConnectionFactory);
    }

    public static void main(String[] args) {
//        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//        byte[] serialize = jsonSerializer.serialize(user);
//        System.out.println("GEN_JACK="+new String(serialize,Charset.forName("UTF8")));
//        BootUser deserialize = jsonSerializer.deserialize(serialize, BootUser.class);
//
//        byte[] serialize1 = jackson2JsonRedisSerializer.serialize(user);
//        System.out.println("JACK="+new String(serialize1,Charset.forName("UTF8")));
//        Object deserialize1 = jackson2JsonRedisSerializer.deserialize(serialize);
//
//
//        JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer(RedisConfig.class.getClassLoader());
//        byte[] jdk = jdkSerializationRedisSerializer.serialize(123);
//        System.out.println("jdk="+new String(jdk,Charset.forName("UTF8")));
//
//
//        Hessian2SerializationRedisSerializer hessian2SerializationRedisSerializer = new Hessian2SerializationRedisSerializer();
//        byte[] serialize2 = hessian2SerializationRedisSerializer.serialize(123);
//        System.out.println("hessian="+new String(serialize2));

//        BootUser user = new BootUser();
//        user.setAge(12);
//        user.setId(1);
//        user.setName("杜国庆");
//
        init();
//        jdkTemplate.opsForValue().set("jdk",user);
//        jackTemplate.opsForValue().set("gen_jack",user);
//        jackson2Template.opsForValue().set("jack",user);
//        hessianTemplate.opsForValue().set("hessian",user);
//        myStringRedisTemplate.opsForValue().set("mystring",JSON.toJSONString(user));
//        stringRedisTemplate.opsForValue().set("string", JSON.toJSONString(user));
//        hessianTemplate.opsForValue().increment("test",1);
        Object test = hessianTemplate.opsForValue().get("test");
        System.out.println(test.toString());
    }




    private static class MyStringRedisTemplate extends RedisTemplate{
        public MyStringRedisTemplate(RedisConnectionFactory connectionFactory) {
            RedisSerializer<String> stringSerializer = new StringRedisSerializer(Charset.forName("GBK"));
            setKeySerializer(stringSerializer);
            setValueSerializer(stringSerializer);
            setHashKeySerializer(stringSerializer);
            setHashValueSerializer(stringSerializer);
            setConnectionFactory(connectionFactory);
            afterPropertiesSet();
        }
    }

    private static class HessianTemplate extends RedisTemplate{
        public HessianTemplate(RedisConnectionFactory connectionFactory) {
            RedisSerializer<String> stringSerializer = new StringRedisSerializer(Charset.forName("UTF8"));
            RedisSerializer hessainSerializer = new Hessian2SerializationRedisSerializer();
            setKeySerializer(hessainSerializer);
            setValueSerializer(stringSerializer);
            setConnectionFactory(connectionFactory);
            afterPropertiesSet();
        }
    }


}
