## IO从操作系统到java再回到操作系统的过程：
<br/>![](resource/zero-copy.png)
## IO 的4中模型总结
<br/>![](resource/IO模型.png)
* 具体是什么模型，其实还是要看站在哪个角度。
  比如：
  我们站在操作系统角度上，其实selector 还是同步非阻塞模型
  站在java 的角度来说，它是多路复用模型
  站在netty的角度来说，它就是异步模型
## reactor 模型总结
* Reactor模型
  <br/>![img.png](resource/reactorModel.png)
* 单reactor单线程模型
  <br/>![SingleReactorModel.png](resource%2FSingleReactorModel.png)
* 单reactor多线程模型（tomcat）
  <br/>![singleReactorMultiThreadModel.png](resource%2FsingleReactorMultiThreadModel.png)
* 主从reactor
  * 形式一
<br/>![img.png](resource/主从reator.png)
  * 形式二
<br/>![img.png](resource/mltiReactorModel.png)
* netty的reator

## zero copy
#### 传统的IO  
~~~code
     -------DMA copy-----    -------CPU copy---     -------------jvm copy ---
    |                    |   |                |    |                         |
hardware buffer   ->  kernel buffer   -> user buffer(direct buffer)  -> heap buffer
~~~
#### mmap技术
* 用户内存的数据不需要CPU copy
* 读写user buffer时，直接映射到kernel buffer
~~~code
     -------DMA copy-----    -------mmp-------      -------------jvm copy ---
    |                    |   |                |    |                         |
hardware buffer   ->  kernel buffer   -> user buffer(direct buffer)  -> heap buffer
~~~
#### send file
~~~
     -------DMA copy-----     ------copy desc--     ---DMA copy-----
    |                    |   |                |    |               |       
socket buffer   ->  kernel buffer  ->    socket buffer    ->   hardware  
~~~

~~~java
    SocketChannel clientChannel = (SocketChannel)selectionKey.channel();
    ByteBuffer allocate = ByteBuffer.allocate(1024);
    try {
       while (clientChannel.read(allocate)>0){
            
       }
    
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
~~~
read 该read多少呢？ 可以read的字节数是多少呢？ 一个数据包？多个数据包？ 和TCP 粘包/拆包有关系吗？
