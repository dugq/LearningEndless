package com.example.learn.jreApi.atomic;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by dugq on 2023/4/25.
 */
public class AtomicReferenceTest {

    public static AtomicReference<User> atomicUserRef = new AtomicReference<User>();

    @Test
    public void testAtomicReferenceTest(){
        User user = new User("conan",15);
        atomicUserRef.set(user);
        User updateUser = new User("Shinichi", 17);
        atomicUserRef.compareAndSet(user, updateUser);
        System.out.println(atomicUserRef.get().getName());
        System.out.println(atomicUserRef.get().getOld());

    }

    static class User {
        private final String name;
        private final int old;
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
