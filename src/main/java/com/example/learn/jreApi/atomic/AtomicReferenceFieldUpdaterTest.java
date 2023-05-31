package com.example.learn.jreApi.atomic;

import com.example.demo.spring.pojo.entry.User;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * Created by dugq on 2023/4/25.
 */
public class AtomicReferenceFieldUpdaterTest {

    private static AtomicReferenceFieldUpdater<User,String> atomicUser = AtomicReferenceFieldUpdater.newUpdater(User.class,String.class,"name");

    @Test
    public void test(){
        User user = new User("tom",1);
        // tom
        System.out.println(atomicUser.get(user));
        System.out.println(atomicUser.compareAndSet(user,"tom","jack"));
        // jack
        System.out.println(atomicUser.get(user));
        atomicUser.set(user,"tom");
        //tom
        System.out.println(atomicUser.get(user));
        // tom
        System.out.println(atomicUser.getAndSet(user,"jack"));
        // jack
        System.out.println(atomicUser.get(user));
    }






    static class User {
        public volatile String name;
        public   int old;
        public User(String name,int old) {
            this.name = name; this.old = old;
        }
        public String getName() {
            return name;
        }
        public int getOld() {
            return old;
        }
    }

}
