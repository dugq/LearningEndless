package com.dugq.jreApi.classload;


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
        //getClassLoader 返回值null 代表bootstrap class load
        // 为什么是返回null呢？ 是不是就是为了逃避我想干的事：bootstrapClassLoader.getParent();
        final ClassLoader thisClassLoad = ClassLoadTest.class.getClassLoader();
        System.out.println("app class load:"+thisClassLoad);
        System.out.println("app class load parent:"+thisClassLoad.getParent());
        System.out.println("系统加载器："+ClassLoader.getSystemClassLoader());
        final ClassLoader LoadedSystemClassLoader = ClassLoader.getSystemClassLoader().getClass().getClassLoader();
        System.out.println("system class load 由谁加载："+LoadedSystemClassLoader);
//        System.out.println(LoadedSystemClassLoader.getClass().getName());
        System.out.println("class load 由谁加载："+thisClassLoad.getClass().getClassLoader());
    }
}
