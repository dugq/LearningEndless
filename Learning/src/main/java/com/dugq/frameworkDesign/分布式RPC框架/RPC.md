## RPC负责的事
* 序列话
* 多线程/并发 发起IO
* 锁处理资源竞争
* 流量控制
* 负载均衡
## 协议
* **dubbo**
  * dubbox
  * motan
* **grpc**
* **sofa-pbrpc**
* hessian
* http
  * 优势：
    * 协议很成熟，tomcat，jboss等服务提供端，httpclinet等客户端都很齐全
  * 劣势：
    * 协议复杂，冗余字段较多，传输文本较大
* rmi
* web service
* thrift
* memcached
* redis


## 序列化
* json
  * 和xml一样，有点在于简单且结构一目了然
  * 缺点在于序列化面向字符串，效率不高
* xml
* protocol buffers
  * 语言无关，平台无关。linux等多个平台都可支持，支持Java，C++等多种语言
  * 高效 序列化更快，序列化后的字节数更少
  * 安全性。数据经过编码后，抓包获取的字节流不好解析
  * 扩展性，兼容性好。类似json
  * 缺点1：使用起来不是很方便，而且序列话后的文本看不懂
  * 缺点2:扩展性比json差，只适合追加字段
* Kryo
  * 性能上 比json 好，比protostuff差
  * 扩展性不好，不能修改字段，跨语言能力差
* FST
  * 
