# 虚拟机执行的流程

* 1、[javaC](compile/compile.md)将.java文件编译为[java字节码指令](instruction/instruction.md)组成的.class文件
* 2、虚拟机启动完成后调用main方法所在文件
* 3、通过[类加载器](classload/classload.md)加载main类文件以及其依赖的所有类文件
* 4、创建主线程，开辟[线程栈](stack/stack.md)，执行方法
* 5、[字节码执行引擎](explain/explain.md)将字节码指令翻译为机器码执行
* 6、在解释执行的过程中，[JVM会对指令集合进行恰当的优化](codeOptimization/CodeOptionmization.md)
