package com.example.learn.jreApi.hash;

import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dugq on 2023/4/17.
 */
public class ConcurrentHashMapTest {


    @Test
    public void testResize(){
        ConcurrentHashMap map = new ConcurrentHashMap(64);
        new Thread(()->{
            for (int i =0 ;i < 300; i++){
                map.put(i,new Object());
            }
        }).start();
        new Thread(()->{
            for (int i =300 ;i < 600; i++){
                map.put(i,new Object());
            }
        }).start();
        new Thread(()->{
            for (int i =600 ;i < 900; i++){
                map.put(i,new Object());
            }
        }).start();
        new Thread(()->{
            for (int i =900 ;i < 1200; i++){
                map.put(i,new Object());
            }
        }).start();
        new Thread(()->{
            for (int i =120 ;i < 150; i++){
                map.put(i,new Object());
            }
        }).start();
        map.put(100,new Object());
    }

    @Test
    public void remove() throws NoSuchFieldException, IllegalAccessException {
        ConcurrentHashMap map = new ConcurrentHashMap();
        map.put(1,new Object());
        map.put(17,new Object());
        map.remove(1);
    }

    @Test
    public void testForEach(){
        ConcurrentHashMap<Integer,Object> map = new ConcurrentHashMap<>();
        //大前提，hash必须保证在同一个链表中
        map.put(0,new Object());
        map.put(16,new Object());
        map.put(32,new Object());
        map.put(64,new Object());
        map.forEach((key,val)->{
            if (key==0){
                //如果从遍历的当前值连续删除数位，那么后续的删除将无效
                map.remove(0);
                map.remove(16);
                map.remove(32);
                map.remove(64);
            }
            //最终输出结果将无视删除
            System.out.println(key);
        });
    }

    @Test
    public void testForEach2(){
        ConcurrentHashMap<Integer,Object> map = new ConcurrentHashMap<>();
        //大前提，hash必须保证在同一个链表中
        map.put(0,new Object());
        map.put(16,new Object());
        map.put(32,new Object());
        map.put(64,new Object());
        map.forEach((key,val)->{
            if (key==0){
                //如果从遍历的不是当前值连续删除数位，那么删除就是生效的
                map.remove(16);
                map.remove(32);
                map.remove(64);

                //或者从当前位置开始删除，删除是跳跃的，那么不连续的都是有效的
                //map.remove(0);
                //map.remove(32);
            }
            System.out.println(key);
        });
    }

}
