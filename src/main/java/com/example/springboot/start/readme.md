# 启动一个应用程序
### 启动器：SpringApplication。
启动步骤：
* 创建一个自适应的应用上下文。
* 注册一个CommandLinePropertySource以将命令行属性设置为spring属性
* 刷新应用上下文以加载说有的单例bean
* 触发所有的CommandLineRunner beans

*为什么是这一系列步骤？*
##### 应用上下文
spring IOC是spring应用的核心组件，那IOC中ApplicationContent又是核心组件。
所以spring应用启动的第一件事就是创建一个ApplicationContent。
1、那么如何去自适应呢？
 哇，简直坑，就是判定是否包含指定的类。WebApplicationType#deduceFromClasspath





