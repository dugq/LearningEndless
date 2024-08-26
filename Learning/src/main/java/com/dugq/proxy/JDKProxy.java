package com.dugq.proxy;

import sun.misc.ProxyGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Created by dugq on 2024/1/17.
 */
public class JDKProxy implements ProxyService{

    private static String proxySaveLocation = "/Users/duguoqing/dev/tmp/";


    @Override
    public <T> Class<T> generatorClass(Class<T> interfaceClass){
        // 使用 Proxy 生成代理类
        Class<?> proxyClass = Proxy.getProxyClass(JDKProxy.class.getClassLoader(), interfaceClass);
        printClass(proxyClass);
        return (Class<T>) proxyClass;
    }

    @Override
    public <T> T generatorProxy(Class<T> interfaceClass, InvocationHandler handler){
        return (T) Proxy.newProxyInstance(JDKProxy.class.getClassLoader(), new Class[]{interfaceClass}, handler);
    }


    public static void generateClassFile(Class clazz, String proxyName) {
        //根据类信息和提供的代理类名称，生成字节码
        byte[] classFile = ProxyGenerator.generateProxyClass(proxyName, clazz.getInterfaces());
        String paths = clazz.getResource(".").getPath();
        System.out.println(paths);
        FileOutputStream out = null;

        try {
            File file = new File(proxySaveLocation+proxyName + ".class");
            file.createNewFile();
            //保留到硬盘中
            out = new FileOutputStream(proxySaveLocation);
            out.write(classFile);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 运行时动态代理某对象
     */
    public void runtimeProxy(Object target){
        Class<?> interfaces = target.getClass();
        Proxy.newProxyInstance(interfaces.getClassLoader(), interfaces.getInterfaces(), (proxy, method, args) -> {
            System.out.println("proxy before run !");
            Object result = method.invoke(target, args);
            System.out.println("proxy after run !");
            return result;
        });
    }

}
