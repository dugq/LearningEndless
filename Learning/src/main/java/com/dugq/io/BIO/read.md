#BIO
###解释
  java IO的一种实现  
  因为它最被人所熟知的特性就是阻塞式的处理IO，所以大家都叫它阻塞IO  
  java最初的IO都是BIO，在jdk中java.io包下的所有东西都是BIO的模式。

*那么接下来我们的研究的内容就是：阻塞IO，那么它阻塞在哪儿呢？*

 [BIO线程模型示例](BIOServerThreadModel.java) 
 
###BIO模型的发展历程
####1.单线程模型
             server
               |
             thread
               ｜   
             clinet
             
此模型用于一些小型单片机上，服务器为单线程模式，一次只处理一个请求  
缺陷： 不能并发 
####2.多线程模型              
             server               
           /   ｜   \               
          /    ｜    \              
    thread   thread  thread       
      |        |        |
    clinet   clinet   clinet
缺陷： 
* 每个客户端连接进来都需要新建一个线程，消耗太大
* 虽然能够并发，但是并发量不能过高，否则将发生OOM

####3.线程池模型
             server
               ｜
           threadPool               
           /   ｜   \               
          /    ｜    \              
    thread   thread  thread       
      |        |        |
    clinet   clinet   clinet
此模型为当前BIO模式下最优线程模型。以此模型，我们来实现一个服务器。

###BIO Blocking在哪里？
首先我们定义：当线程被挂起不能继续执行代码时，就认为线程当前出于阻塞状态。 
- serverSocket.accept();
- socket.getInputStream().read(byte[]);  
*如何改进呢？*
