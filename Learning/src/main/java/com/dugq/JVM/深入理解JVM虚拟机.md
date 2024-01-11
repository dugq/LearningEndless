# JDK
* jdk的整体架构大致如下 ![](./resource/JDK架构.png)
* 从图中可以看出，JVM是实现跨平台能力(屏蔽平台差异)的关键
* JVM底层由三个系统构成分别是：类加载、运行时数据区、执行引擎。![](./resource/JVM.png)

# JVM --- java虚拟机
| what           | why                                 | how               | 
|----------------|-------------------------------------|-------------------|
| 为java字节码提供运行环节 | java是高级语言,代码不能在操作系统上直接执行，更不能在CPU上执行 | 执行方法所请求的指令和运算     |
|                |                                     | 定位、加载和验证新的类型（类加载） |
|                |                                     | 管理应用内存，包括堆、栈、方法区等 |

### 1、[java运行时内存区域](./runtimeMem/mem.md)

### 2、[classLoad类加载子系统](vmRunSystem/classload/classload.md)

### 3、[编译解释执行](vmRunSystem/compile/compile.md)
