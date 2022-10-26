#Socket

##介绍  
跟操作系统进行交互，完成连接建立和数据读写。我们可以把它看成一系列网络协议的实现。
比如 ServerSocket & Socket 就是TCP协议的实现。

##[Socket通信示例](SocketTest.java)

#### ServerSocket:  
  **类注释描述如下：**
  * 这个类实现服务器Socket。服务器套接字等待通过网络进入的请求。 
  * 它基于该请求执行一些操作，然后可能向请求者返回结果。 
  * 服务器套接字的实际工作是由SocketImpl类的一个实例执行的。 
  * 应用程序可以更改创建套接字实现的套接字工厂，以配置自己来创建适合本地防火墙的套接字。  
 
*从这些注释可知，该类只用于服务器接受请求，并且可以执行相关逻辑后，将结果返回给客户端。对于Socket的接收写入等操作，实际由一个可配置的SocketImpl对象完成。如果我们需要对socket功能进行修改和增强，只需要配置SocketImpl对象即可。* 
  
**然后我们在看看构造函数**
~~~java
 public class ServerSocket implements java.io.Closeable {
    
    public ServerSocket(int port, int backlog, InetAddress bindAddr) throws IOException{
        setImpl();
        if (port < 0 || port > 0xFFFF)
            throw new IllegalArgumentException(
                    "Port value out of range: " + port);
        if (backlog < 1)
            backlog = 50;
        try {
            bind(new InetSocketAddress(bindAddr, port), backlog);
        } catch(SecurityException e) {
            close();
            throw e;
        } catch(IOException e) {
            close();
            throw e;
        }
    }

}
~~~
*这是构建一个已经绑定IP：port的构造器。其他未执行绑定的构造器，和此构造器基本一致，需要用户手动执行绑定。但在绑定之前可以通过setSocketFactory方法自定义SocketImpl，这里不做过多解释。*
  * port：创建的服务器Socket绑定的本地端口号。0：自动获取可用端口。端口区间：0 ～ 65535
  * backlog： 请求的传入连接队列的最大长度。如果连接指示到达时队列已满，则连接将被拒绝
  * bindAddr： 服务器的本地地址对象 

构造器的内容还是比较简单：
  * setImpl方法: 实际就是创建SocketImpl对象的。
  * bind：绑定本地IP和端口

  我们来看看bind方法：
~~~java
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.net.InetSocketAddress;

public class ServerSocket implements java.io.Closeable{
      public void bind(SocketAddress endpoint, int backlog) throws IOException {
        if (isClosed())
            throw new SocketException("Socket is closed");
        if (!oldImpl && isBound())
            throw new SocketException("Already bound");
        if (endpoint == null)
            endpoint = new InetSocketAddress(0);
        if (!(endpoint instanceof InetSocketAddress))
            throw new IllegalArgumentException("Unsupported address type");
        InetSocketAddress epoint = (InetSocketAddress) endpoint;
        if (epoint.isUnresolved())
            throw new SocketException("Unresolved address");
        if (backlog < 1) 
            backlog = 50;
        try {
            SecurityManager security = System.getSecurityManager();
          if (security != null)
            security.checkListen(epoint.getPort());
          getImpl().bind(epoint.getAddress(), epoint.getPort());
          getImpl().listen(backlog);
          bound = true;
        } catch(SecurityException e) {
          bound = false;
          throw e;
        } catch(IOException e) {
          bound = false;
          throw e;
        }
      }
}
~~~
 该方法会执行一系列的验证，以及安全方法的处理。这里不展开说明。
 重要部分还是对SocketImpl对象进行本地端口的绑定，以及开始监听请求。
#### InetSocketAddress 
   我们=可以看成是IP协议的实现，转 [ip协议](../ip/readme.md)

#### SocksSocketImpl: SOCKS (V4 & V5) TCP socket implementation (RFC 1928)
     

#### Socket: 客户端sockets的实现

*socket.accept() 在做什么？*

*Stream(InputStream/OutputStream)这里的数据是哪里来的，又去向了哪里？*

*[IO过程](../image/277b6e31-26f0-437a-899f-202f5e65cd5e.png)是如何实现的？*

*Socket 完成了应用和操作系统之间的IO交互，怎么交互的呢？*

##[BIO](../BIO/read.md)


##[NIO](../NIO/readme.md)
