package com.dugq.proxy;

import com.alibaba.fastjson2.JSON;
import com.dugq.base.User;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Loader;
import javassist.Modifier;
import javassist.NotFoundException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by dugq on 2024/1/17.
 *
 * <a href="https://www.javassist.org/tutorial/tutorial.html#read">javaassist官方文档</a>
 */
public class JavaassitProxy {
    private String name = "test-name";

    public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        // 测试生成一个类
        JavaassitProxy javaassitProxy = new JavaassitProxy();
        Class<MyUser> MyUserImpl = javaassitProxy.testGenClass(MyUser.class);
        MyUser o = MyUserImpl.newInstance();
        o.setName("dugq");
        System.out.println(o.getName());

        // 测试修改之前生成的类
        Class<?> modifyUserImpl = javaassitProxy.testModifyClass(MyUserImpl);
        MyUser newUser = (MyUser)modifyUserImpl.newInstance();
        newUser.setName("TOM");
        ((Person)newUser).say();

        // 测试修改一个已经存在的类
        Class<?> aClass3 = javaassitProxy.testModifyClass(JavaassitProxy.class);
        Person person = (Person)aClass3.newInstance();
        person.say();

        //使用默认的pool，它使用的是systemClassLoad进行加载类的
        Class<?> sysmtemGetMyUserImpl = ClassLoader.getSystemClassLoader().loadClass("MyUserImpl");
        // 默认使用systemClassLoad进行加载，两类相同
        System.out.println("system == source ? "+(sysmtemGetMyUserImpl==MyUserImpl)+ "class load = " +sysmtemGetMyUserImpl.getClassLoader());

        // 使用javaassist的类加载器进行toClass。 但这有个坑！！！它会把父类和接口也重新生成。
        // 文档 3.3 Using javassist.Loader 有提到
        // 所以这个类不是MyUser的子类哦。除了反射，没办法用了。
        Class<MyUser> MyUserImplLoadByAssist = javaassitProxy.testJavaAssistLoad(MyUser.class);
        System.out.println("  MyUserImplLoadByAssist"+MyUserImplLoadByAssist.getClassLoader());

        //当然你可以自定义类加载器,这里不再继续深究了
        // 。。。。


        // 最后来个实际点的。例如事务的实现
        TestInterface testInterface = testProxy(new TestClass(), TestInterface.class);
        List<User> users = testInterface.selectAll();
        User user = testInterface.selectById(1L);
        System.out.println(JSON.toJSONString(users));
        System.out.println(JSON.toJSONString(user));
    }

    private static <T> T testProxy(Object proxy,Class<T> interfaces) {
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass ctClass = pool.makeClass(proxy.getClass().getName() + "Proxy");
            CtClass proxyClass = pool.getCtClass(proxy.getClass().getName());
            CtField ctField = new CtField(proxyClass,"proxy",ctClass);
            ctClass.addField(ctField);

            CtConstructor ctConstructor = new CtConstructor(new CtClass[]{proxyClass},ctClass);
            ctConstructor.setBody("this.proxy = $1;");
            ctClass.addConstructor(ctConstructor);

            ctClass.addInterface(pool.get(interfaces.getName()));
            Method[] declaredMethods = interfaces.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                Class<?> returnType = declaredMethod.getReturnType();
                CtClass returnTypeClass = pool.get(returnType.getName());

                Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
                CtClass[] parametersCtClass = new CtClass[parameterTypes.length];
                StringBuilder body = new StringBuilder("{System.out.println(\"Aop before\"); return proxy.");
                body.append(declaredMethod.getName());
                body.append("(");
                for (int i =0; i< parameterTypes.length;i++) {
                    parametersCtClass[i] = pool.get(parameterTypes[i].getName());
                    body.append("$").append(i + 1);
                    if (i<parameterTypes.length-1){
                        body.append(",");
                    }
                }
                body.append(");}");
                CtMethod ctMethod = new CtMethod(returnTypeClass,declaredMethod.getName(), parametersCtClass,ctClass);
                ctMethod.setModifiers(Modifier.PUBLIC);
                ctMethod.setBody(body.toString());
                ctClass.addMethod(ctMethod);
            }
            ctClass.writeFile(".");
            Class<T> targetClass = (Class<T>)ctClass.toClass();
            Constructor<T> constructor = targetClass.getConstructor(proxy.getClass());
            return constructor.newInstance(proxy);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 生成一个新的class
     * @param interfaces 定义规则，方便后续使用。否则生成的class没有声明，无法通过java编译。
     * @return
     */
    public <T> Class<T> testGenClass(Class<T> interfaces) throws CannotCompileException, NotFoundException, IOException {
        // ClassPool pool = new ClassPool();
        // pool.appendSystemPath();
        // 等效，把系统classLoad设置为javaassit classLoad的parent。
        // 这样就可以查找已经存在的class了。
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.makeClass("MyUserImpl");
        CtClass string = pool.get("java.lang.String");
        CtField field = new CtField(string,"name",cc);
        field.setModifiers(Modifier.PRIVATE);
        cc.addField(field);

        String name = interfaces.getName();
        CtClass interf = pool.get(name);
        cc.addInterface(interf);

        CtMethod getName = new CtMethod(string,"getName",null,cc);
        getName.setModifiers(Modifier.PUBLIC);
        getName.setBody("return this.name;");
        cc.addMethod(getName);

        CtMethod setName = new CtMethod(CtClass.voidType,"setName",new CtClass[]{string},cc);
        setName.setModifiers(Modifier.PUBLIC);
        // 好像没办法设置参数名
        setName.setBody("this.name = $1;");
        cc.addMethod(setName);
        // 可以输出字节码也可以不输出
        //cc.writeFile(".");
        return (Class<T>) cc.toClass();
    }


    /**
     * 给指定的类追加一个方法：void say()
     * 为了使得新的方法能通过编译调用，该类追加实现接口 Person
     */
    public Class<?> testModifyClass(Class<?> clazz){
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass ctClass;
            try {
                // 我们可能可以对一个pool.get()的ctClass 进行修改，但这绝对不是有效的做法
                // 一方面正常情况下jvm不会加载重名的class
                // 另一方面 这本身就不符合逻辑，虽然我们可以利用自定义的classLoad去实现，或者卸载重新装载，这都是不安全的，容易引起未知的bug
                // 因此我们更期待的是在原有的基础上进行二次修改，并改名重新定义
                // getAndRename 中的get不会走缓存，也就是说不存在源码的class是不能被查询到的。
                ctClass = pool.getAndRename(clazz.getName(),"CanSay");
            }catch (Exception e){
                CtClass source = pool.getCtClass(clazz.getName());
                if (source.isFrozen()){
                    source.defrost();
                    source.setName(clazz.getName()+"New");
                    ctClass = source;
                }  else{
                    return null;
                }
            }

            CtClass person = pool.get(Person.class.getName());
            ctClass.addInterface(person);
            CtMethod say = new CtMethod(CtClass.voidType,"say",null,ctClass);
            say.setModifiers(Modifier.PUBLIC);
            say.setBody("System.out.println(\"my name is \"+this.name);");
            ctClass.addMethod(say);
//            ctClass.writeFile(".");
            return ctClass.toClass();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public <T> Class<T> testJavaAssistLoad(Class<T> interfaces) throws CannotCompileException, NotFoundException, IOException, ClassNotFoundException {
        // ClassPool pool = new ClassPool();
        // pool.appendSystemPath();
        // 等效，把系统classLoad设置为javaassit classLoad的parent。
        // 这样就可以查找已经存在的class了。
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.makeClass("MyUserImpl");
        CtClass string = pool.get("java.lang.String");
        CtField field = new CtField(string,"name",cc);
        field.setModifiers(Modifier.PRIVATE);
        cc.addField(field);

        String name = interfaces.getName();
        CtClass interf = pool.get(name);
        cc.addInterface(interf);

        CtMethod getName = new CtMethod(string,"getName",null,cc);
        getName.setModifiers(Modifier.PUBLIC);
        getName.setBody("return this.name;");
        cc.addMethod(getName);

        CtMethod setName = new CtMethod(CtClass.voidType,"setName",new CtClass[]{string},cc);
        setName.setModifiers(Modifier.PUBLIC);
        // 好像没办法设置参数名
        setName.setBody("this.name = $1;");
        cc.addMethod(setName);
        // 可以输出字节码也可以不输出
        //cc.writeFile(".");

        Loader cl = new Loader(ClassLoader.getSystemClassLoader(),pool);
        return (Class<T>) cl.loadClass("MyUserImpl");
    }





    public interface MyUser{
        void setName(String name);
        String getName();
    }

    public interface Person{
        void say();
    }

}
