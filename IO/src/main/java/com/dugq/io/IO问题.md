##### 什么是NIO？

<details>
    <summary>
        解题思路
    </summary>

不要局限与网络IO，NIO在文件IO方面提升的效率远高于网络IO        

</details>
<details>
    <summary>
        百度百科
    </summary><br>

1. NIO全称：非阻塞IO，即java中的同步非阻塞IO模型。
2. NIO核心包含3大组件：channel、buffer、selector
   * channel ： IO通道。相较于传统IO，channel是双向通道，可读可写。当然有些时候也可以把它设置为可读不可写或者可写不可读。
   * buffer：数据缓冲区。任何需要读写的数据都需要先放入到buffer中再进行处理。是channel可以双向读写的基础。
   * selector: 监听器。是多路复用模型的核心。赋予NIO可以使用少量线程处理大量连接而无需遍历的基础。
   
</details>

##### NIO 和 BIO有什么区别？

<details>
    <summary>
        解题思路
    </summary>

        

</details>
<details>
    <summary>
        百度百科
    </summary><br>

1. BIO在IO操作会使得线程阻塞。而NIO可以选择非阻塞模式，在IO未准备好时可以选择处理其他事情。
2. NIO提供selector组件，使得NIO可以使用少量线程处理大量连接，而BIO通常一个线程处理一个连接。
3. BIO的读写是单向的，而NIO可以是双向读写的。性能上优于BIO。
4. 使用NIO的多路复用模型可以实现reactor模型，从而大大提升系统的IO处理能力。

</details>

##### 序列化和编解码的区别？

<details>
    <summary>
        解题思路
    </summary>

        

</details>
<details>
    <summary>
        百度百科
    </summary><br>

1. 目的不同： 
   * 序列话主要是为了处理对象和字节流的转换
   * 编解码是为了数据传输过程中数据的准确性而产生的。比如消息格式、长度等。如RPC协议中，底层协议将序列话后的字节流打包以便在网络中进行传输，并在接受端可以完整的还原字节流。
2. 层级不同
   * 序列话往往在编解码的上层工作。
</details>

##### TCP VS UDP?

<details>
    <summary>
        解题思路
    </summary>

        

</details>
<details>
    <summary>
        百度百科
    </summary><br>

1. TCP和UDP都是7层协议中的传输层协议。TCP是为了可靠性传输，而UDP的目标是高效无延迟。
2. TCP是面向连接的，通信双方需要先经过3次握手建立通道，依托管道可以双向通信。而UDP是无连接的，想发就发。
3. TCP提供的可靠性包括：无差错、有序、不丢失、不重复，而UDP则尽最大努力交付。
4. TCP是点对点通信，而UDP则可以1对1，也可以1对多甚至多对多
5. 首部结构不同。TCP的首部要比UDP复杂。

</details>

##### 什么是TCP？TCP的原理是什么？

<details>
    <summary>
        解题思路
    </summary>

1. 目标
2. 通道创建与断开
3. 可靠性

</details>
<details>
    <summary>
        百度百科
    </summary><br>

1. TCP是工作在7层协议中传输层的通信协议，目标是完成可靠性传输。其可靠性表现在：无差错、有序、不丢失，不重复。
2. TCP是面向通道的技术，这也是它实现可靠性的手段。依赖通道，通信双发可以互相通信。
3. TCP可靠性：
   * 通过3次握手完成来完成安全通道的建立
   * 然后通过每条消息通过ACK回复来确保消息收到
   * 最后通过4次挥手保证通道正确关闭。
</details>

##### TCP协议如何保证可靠传输？

<details>
    <summary>
        解题思路
    </summary>

        

</details>
<details>
    <summary>
        百度百科
    </summary><br>

0. 通道再发送数据前，先通过3次握手确保通道可靠
1. 消息体包含：校验和，应答标记和序列号。通过需要保证有序，通过校验和确保消息准备，通过ACK确保消息确实收到
2. 超时重传：如果发送端在一段时间后未收到接收方的应答，就会重新传输数据包，接收端可以通过序列号确保消息不重复
3. 滑动窗口：旧版TCP每次只发送一条消息，确保消息收到后，再发一下个数据包，但这样传输太慢，于是采用滑动窗口的方式连续发送
4. 拥堵控制：滑动窗口的方式传输快了，但可靠性下降了，于是TCP可以根据实时情况，动态控制窗口的大小，确保拥堵时，不会大批量丢失或重传
5. 慢启动：  当通道刚建立好后，开始时，滑动窗口比较小，随着响应的及时层度，逐步增加窗口的大小
6. 快速恢复： 当拥塞缓解后，窗口的大小快速回复到历史最大值

其中3、6 更偏向于优化
</details>

##### 在TCP中，什么是粘包、拆包？TCP如何处理？

<details>
    <summary>
        解题思路
    </summary>

        

</details>
<details>
    <summary>
        百度百科
    </summary><br>

###### 粘包、拆包发生的原因：
1. 上层需要传输的数据大于缓冲区的容量
2. 数据包大于MSS
3. 单次发送的数据太小，影响网络效率

###### 解决办法
1. 在完整的数据包头部的固定位置添加完整数据包的大小，这样接收端可以根据数据大小重组数据包
2. 每个数据包的大小固定，接收端根据固定大小重组数据包
3. 在数据包之间插入特殊字符，在接受端根据特殊字符分组数据包
4. 通过特殊协议定义数据包。比如json格式，每个完整的json对象就是一个数据包。java序列化等
</details>

##### 网络IO的线程模型有那些？

<details>
    <summary>
        解题思路
    </summary>

        

</details>
<details>
    <summary>
        百度百科
    </summary><br>

1. 同步阻塞模型。 一个线程管一个连接，有数据处理业务，没数据一直挂起等待
2. 同步非阻塞模型。一个线程惯着一个连接，线程定期检查连接是否有数据，有数据就处理业务，没数据可以去干别事情，或者死循环检测。
3. 多路复用模型。将多个连接注册到一个selector上，这个线程一直轮训检测是否有事件发送，有就处理业务，没有就绪检测
4. 异步模型。有连接接入或者读写事件时，通知线程启动执行任务
</details>

##### reactor线程模型

<details>
    <summary>
        解题思路
    </summary>

        

</details>
<details>
    <summary>
        百度百科
    </summary><br>

reactor线程模型是基于多路复用模型实现的线程模型
1. 单selector单线程模型。一个连接监听多个连接，有事件处理事件，没事件就等待
2. 单selector多线程模型。一个线程专门负责监听多个连接，有事件时将连接和事件丢给另一个专门负责处理任务的线程池处理。
3. 主从模型。将事件分为accept事件和read/write事件，主线程一般采用单线程模式，负责监听accept事件，并注册到从selector上，从线程负责监听连接的读写事件，并将事件交给另一线程池处理。
4. netty的主从模型。
   * netty 的主线程也采用多线程多selector模式，但是每个线程的selector只负责监听一个端口上的accept事件
   * netty的从线程也是多线程多selector模型，主线程收到的连接注册到一个selector上，从线程即负责连接的读写事件也负责业务处理，开发者可根据需求定制时间比例。

</details>

##### selector使用的操作系统函数及其区别？

<details>
    <summary>
        解题思路
    </summary>

   select 、poll 、 epoll     

</details>
<details>
    <summary>
        百度百科
    </summary><br>

1. select函数。它依赖的是bitmap（位图）进行操作符管理的，这种方式可以快速定位fd，但是缺点就是使用麻烦，而且利用32个int存储fd，上限只有1024个位。
   * 将需要监听的连接也就是文件描述符构建为fd_set传入到select函数中。当有事件发生时，fd_set中的fd_mark所对应的的状态位会发生改变
   * select最多可以接受一个32节点的fd_set数组，每个fd_set可以管理32个fd。
   * 在有事件发生后，需要遍历先fd_set再遍历fd_mark位找到具体的连接。
2. poll函数。它基本可以说是select的升级版本，它不再使用bitmap实现，而是使用fd数组进行参数传递。
3. epoll函数。epoll就不再是简单的单个函数了，而是一组函数。已经具备面向对象的思想了。
   * epoll创建后，可以动态的对监听的集合进行修改，不再像select和poll每次需要构建新的数组
   * epoll监听返回后，可以直接获取到有事件的fd，而无需像select和poll一样要遍历监听列表
</details>

##### Netty跟Java NIO有什么不同，为什么不直接使用JDK NIO类库？

<details>
    <summary>
        解题思路
    </summary>

        

</details>
<details>
    <summary>
        百度百科
    </summary><br>

1. Netty基于NIO实现了一个事件驱动型异步IO框架。换句话说，如果直接使用NIO，那无非是自己再造一个类似netty的架构罢了
2. Netty框架对于使用NIO中常见问题都提供了多种有效的解决方案。比如：buffer的使用，粘包拆包、编码解码等
3. Netty的自定义的reactor模型在高并发场景表现很好。
4. Netty还对NIO进行了优化，以及NIO的问题进行解决。比如：空轮训问题。

</details>

##### Netty高性能体现在哪些方面？

<details>
    <summary>
        解题思路
    </summary>

        

</details>
<details>
    <summary>
        百度百科
    </summary><br>

1. Netty独特的reactor线程模型，使得Netty可以用极少的线程处理大量的连接。这无疑降低了线程上下文切换的损耗。
2. Netty底层依赖的是Java的NIO体系，这使得它在IO处理上非常高效
3. 优秀的内存使用，Netty对buffer进行缓存，无论是直接内存还是堆内存都进行了池化技术，这极大的降低了IO数据缓冲区的创建和释放成本。此外，它还提供了CompositeByteBuf实现零copy
</details>

##### Netty的线程模型是什么样子的？

<details>
    <summary>
        解题思路
    </summary>

        

</details>
<details>
    <summary>
        百度百科
    </summary><br>

Netty的线程模型设计是非常优秀的，Netty启动时需要配置两个EventLoop
1. 如果我们把两个EventLoop设置成同一个单线程EventLoop，此时Netty的模型就是单线程reactor模型
2. 如果我们把两个EventLoop设置成同一个多线程EventLoop，此时就是多线程react模型
3. 如果把两个EventLoop配置为两个多线程EventLoop，此时就是主从多线程react模型
4. 当然也可以搞成主从单线程react模型
</details>

##### netty的内存池是什么样子的？

<details>
    <summary>
        解题思路
    </summary>

        

</details>
<details>
    <summary>
        百度百科
    </summary><br>

1. netty内部维护了一个堆内缓冲池和一个直接内存缓冲池。
2. netty的缓冲池内部维护了多个poolArea在启动时以及初始化好，默认和线程数保持一致。
3. 每个poolArea内部维护多个poolChunk，也就是地址连续的内存块。
4. 当netty需要分配一个buffer时，会选择一个使用率最小的poolArea，或者和当前线程绑定poolArea
5. 从当前的poolArea中选择一个空间足够的poolChunk,如果没有则创建一个新的。
6. poolChunk的设计是按照8kb的固定大小分配为page，把这些page当成叶子节点组成一个完全平衡二叉树。
7. 然后利用完全平衡的特性，可以快速定位到所需内存大小的层，从该层中选择一个未使用的节点分配给你即可。
</details>
