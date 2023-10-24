package com.example;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by dugq on 2023/10/13.
 */
public class JedisClientFactory {

    public static Jedis getJedis(){
        return new Jedis("127.0.0.1",6379);
    }

    static JedisPool jedisPool;
    static {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(10);
        config.setMaxIdle(10);
        config.setMinIdle(0);
        config.setMaxWaitMillis(1000);
        jedisPool = new JedisPool(config,"127.0.0.1",6379,1000);
    }

    public static Jedis getJedisFromPool(){
        return jedisPool.getResource();
    }

}
