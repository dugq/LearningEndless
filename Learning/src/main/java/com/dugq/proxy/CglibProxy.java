package com.dugq.proxy;

import com.dugq.base.User;
import net.sf.cglib.core.DefaultGeneratorStrategy;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;

/**
 * Created by dugq on 2024/1/17.
 */
public class CglibProxy implements ProxyService{

    @Override
    public <T> Class<T> generatorClass(Class<T> interfaceClass) {
        Enhancer enhancer =new Enhancer();
        enhancer.setStrategy(new MyDefaultGeneratorStrategy());
        enhancer.setInterfaces(new Class[]{interfaceClass});
        return enhancer.createClass();
    }

    @Override
    public <T> T generatorProxy(Class<T> interfaceClass, InvocationHandler handler) {
        Enhancer enhancer =new Enhancer();
        enhancer.setStrategy(new MyDefaultGeneratorStrategy());
        enhancer.setInterfaces(new Class[]{interfaceClass});
        enhancer.setCallback((MethodInterceptor) (object, method, args, methodProxy) -> {
            return handler.invoke(object,method,args);
        });
        Object object = enhancer.create();
        printClass(object.getClass());
        return (T) object;
    }


    public int maxSubArray(int[] nums) {
        int maxValue = 0;
        int length = nums.length;
        int dp[][] = new int[length][length];
        for(int i =0 ;i < nums.length; i++){
            for(int j=i;j<length;j++){
                if(j==i){
                    dp[i][j] = nums[j];
                }else{
                    dp[i][j] = dp[i][j-1]+nums[j];
                }
                maxValue = Math.max(maxValue,dp[i][j]);
            }
        }
        return maxValue;
    }


    private void testMethodInvoke() {
        try {
            Son son = new Son("张三","faasdfasdfasdf",1);
            Method method = User.class.getDeclaredMethod("getUsername");
            System.out.println("my name is "+method.invoke(son));
            System.out.println("my name is "+son.getUsername());
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public <T> T testproxy(Class<T> clazz){
        Enhancer enhancer =new Enhancer();
        enhancer.setStrategy(new MyDefaultGeneratorStrategy());
        enhancer.setSuperclass(clazz);
        enhancer.setCallback((MethodInterceptor) (obj1, method, args, proxy) ->{
            System.out.println("before proxy");
            Object invoke = proxy.invokeSuper(obj1,args);
            System.out.println("after proxy");
            return invoke;
        });
        return (T)enhancer.create();
    }



    class MyDefaultGeneratorStrategy extends DefaultGeneratorStrategy {
        public MyDefaultGeneratorStrategy() {
            super();
        }

        @Override
        protected byte[] transform(byte[] b) throws Exception {
            URL resource = this.getClass().getClassLoader().getResource("");
            String path = resource.getPath();
            File file = new File(path+"/"+ RandomUtils.nextInt(1,1000)+".class");
            IOUtils.write(b, Files.newOutputStream(file.toPath()));
            return super.transform(b);
        }

    }

    class Son extends User{

        public Son(String username, String password, int uid) {
            super(username, password, uid);
        }

        @Override
        public String getUsername() {
            return "Bob";
        }
    }
}
