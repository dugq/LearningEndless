# 类加载器
问题：程序启动时的完整流程时什么样子的？
     main方法所在的类在什么时候加载，由谁来加载？
     ClassLoad也是一个class，它由谁来加载？

### 意义  
使得Java 类可以被动态加载到 Java 虚拟机中并执行  
### 执行过程
* Java 源程序（.java 文件）在经过 Java 编译器编译之后就被转换成 Java 字节代码（.class 文件）
* 类加载器负责读取 Java 字节代码，并转换成 java.lang.Class 类的一个实例。每个这样的实例用来表示一个 Java 类
* 通过此实例的 newInstance()方法就可以创建出该类的一个对象  
*实际应用：比如 Java 字节代码可能是通过工具动态生成的，也可能是通过网络下载的。*

### 重点
在一个class实例被构建成功后，它会记载这构建它的类加载器，
而在实际中，JVM 判定两个class实例是同一个实例的依据：
* 1、类的全限定名相同
* 2、加载类的类加载器相同  

不同的类加载器为相同名称的类创建了额外的名称空间。  
相同名称的类可以并存在 Java 虚拟机中，只需要用不同的类加载器来加载它们即可。  
不同类加载器加载的类之间是不兼容的，这就相当于在 Java 虚拟机内部创建了一个个相互隔离的 Java 类空间。  
这种技术在许多框架中都被用到。比如tomcat

但是这样的设计也会带来问题，其一就是
* 所有 Java 应用都至少需要引用 java.lang.Object 类，也就是说在运行的时候，java.lang.Object 这个类需要被加载到 Java 虚拟机中。
如果这个加载过程由 Java 应用自己的类加载器来完成的话，很可能就存在多个版本的 java.lang.Object 类，而且这些类之间是不兼容的
为了解决这个问题，classLoad引入了 **父类委托机制**

### 类加载双亲委派机制
#### 分类
java规范将类加载器分为三类
* 引导类加载器
* 扩展类加载器
* 应用类加载器  
**JVM为每一种分别实现了个加载器** 
* bootstrap classLoad  
它负责将JAVA_HOME/lib下面的核心类库或-Xbootclasspath选项指定的jar包等虚拟机识别的类库加载到内存中。  
由于启动类加载器涉及到虚拟机本地实现细节，开发者无法直接获取到启动类加载器的引用。  
具体可由启动类加载器加载到的路径可通过System.getProperty(“sun.boot.class.path”)查看。
唯有此加载器是不继承java.lang.ClassLoader，它是有原生代码实现。jar包中查看不到
* Extension classLoad  
扩展类加载器是由Sun的ExtClassLoader（sun.misc.Launcher$ExtClassLoader）实现的，它负责将JAVA_HOME /lib/ext或者由系统变量-Djava.ext.dir指定位置中的类库加载到内存中。  
开发者可以直接使用标准扩展类加载器，具体可由扩展类加载器加载到的路径可通过System.getProperty("java.ext.dirs")查看
* System classLoad  
系统类加载器是由 Sun 的 AppClassLoader（sun.misc.Launcher$AppClassLoader）实现的，
它负责将用户类路径(java -classpath或-Djava.class.path变量所指的目录，即当前类所在路径及其引用的第三方类库的路径)下的类库加载到内存中。  
开发者可以直接使用系统类加载器，具体可由系统类加载器加载到的路径可通过System.getProperty("java.class.path")查看。
#### 实现
##### ClassLoader重要方法
```java
public class ClassLoad{
   //加载指定名称（包括包名）的二进制类型，供用户调用的接口 
   public Class<?> loadClass(String name) throws ClassNotFoundException{  } 
   //加载指定名称（包括包名）的二进制类型，同时指定是否解析（但是这里的resolve参数不一定真正能达到解析的效果），供继承用 
   protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException{  } 
   //findClass方法一般被loadClass方法调用去加载指定名称类，供继承用 
   protected Class<?> findClass(String name) throws ClassNotFoundException {  } 
   //定义类型，一般在findClass方法中读取到对应字节码后调用，final的，不能被继承 
   //这也从侧面说明：JVM已经实现了对应的具体功能，解析对应的字节码，产生对应的内部数据结构放置到方法区，所以无需覆写，直接调用就可以了） 
   protected final Class<?> defineClass(String name, byte[] b, int off, int len) throws ClassFormatError{  }
}
```    
其中只有loadClass是对外暴露的方法。我们看看他的实现。  
                
```java
public class ClassLoad{
     protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
             //首先加载的过程是上锁的.锁的设计是并行的，以class的全路径作为锁的条件，可同时加载多个class。
             //在没有对象可锁的情况下，它采用classPath-Object的一个Map进行并发锁，同时使用concurrentHashMap解决高并发问题。
             synchronized (getClassLoadingLock(name)) {
                 // First, check if the class has already been loaded
                 //利用缓存可有效的方式class的重复加载。可以看出jdk也没有特别在意锁的安全，并没有使用双重锁定规则
                 Class<?> c = findLoadedClass(name);
                 if (c == null) {
                     //先使用双亲classLoad进行加载
                     try {
                         if (parent != null) {
                             c = parent.loadClass(name, false);
                         } else {
                             c = findBootstrapClassOrNull(name);
                         }
                     } catch (ClassNotFoundException e) {
                         // ClassNotFoundException thrown if class not found
                         // from the non-null parent class loader
                     }
                     //当双亲未加载到时，再自己进行查找。实际的加载过程是发生在findClass -> defineClass
                     if (c == null) {
                         // If still not found, then invoke findClass in order
                         // to find the class.
                         c = findClass(name);
                     }
                 }
                 if (resolve) {
                     resolveClass(c);
                 }
                 return c;
             }
         }         
}
```
findClass方法要求子类去自定义实现。
defineClass方法是本地方法看不到实现。
ext 和 appclassload都继承自URLClassLoad


