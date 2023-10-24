package com.example;

import redis.clients.jedis.Jedis;

/**
 * Created by dugq on 2023/10/13.
 */
public class Main {
    public static void main(String[] args) {
        testClient();
        testPool();
    }

    private static void testClient() {
        Jedis jedis = JedisClientFactory.getJedis();
        jedis.set("clientTest","dugq");
        System.out.println(jedis.get("clientTest"));
    }

    private static void testPool(){
        Jedis jedis = JedisClientFactory.getJedisFromPool();
        System.out.println(jedis.get("clientTest"));
    }

}
