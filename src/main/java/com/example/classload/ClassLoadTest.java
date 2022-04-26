package com.example.classload;


/**
 * @author dugq
 * @date 2022/2/10 5:55 下午
 */
public class ClassLoadTest {
    public static void main(String[] args) {
        //多个目录时使用英文冒号隔开
        System.out.println("bootstrap:"+System.getProperty("sun.boot.class.path"));
        System.out.println("ext:"+System.getProperty("java.ext.dirs"));
        System.out.println("app:"+System.getProperty("java.class.path"));
        System.out.println("---------------------------------------");
        final ClassLoader thisClassLoad = ClassLoadTest.class.getClassLoader();
        System.out.println(thisClassLoad.getClass().getName());
        final ClassLoader LoadedSystemClassLoader = ClassLoader.getSystemClassLoader().getClass().getClassLoader();
        System.out.println(LoadedSystemClassLoader);
//        System.out.println(LoadedSystemClassLoader.getClass().getName());
    }
}
