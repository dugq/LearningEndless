package com.dugq.jreApi.atomic;

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

        Student student = new Student("tom");
        boolean result = student.setName(student.getName(), "jack");
        System.out.println("更新结果"+result+" 学生："+student.getName());
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

    static class Student {
        private volatile String name;
        public Student(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
        // 属性可访问的地方使用就没有问题
        private static final AtomicReferenceFieldUpdater<Student,String> atomicUser = AtomicReferenceFieldUpdater.newUpdater(Student.class,String.class,"name");

        public boolean setName(String expectedName,String newName){
            return atomicUser.compareAndSet(this,expectedName,newName);

        }
    }

}
