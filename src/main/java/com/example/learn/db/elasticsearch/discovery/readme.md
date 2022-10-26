## DISCOVERY

### 新集群的搭建-集群引导


### 已存在集群加入新节点-节点发现
在es5.0版本以后，es改为**单播**发现机制。  
节点在启动时加入属性 **discovery.seed_hosts**或者**discovery.seed_providers**来配置**master-eligible Node**  
节点在启动后，会依次连接每个**master-eligible Node**，当连接成功后
* 1、新节点先发起请求，验证该节点为**master-eligible Node**，如果该节点为**master-node**，结束，如果该节点不是**master-eligible Node**也结束，进行下一个连接
* 2、如果是**master-eligible Node**然后新节点会依次向该发起**master-eligible Node list**对等匹配请求，被链接的**master-eligible Node**回复是否是master-eligible  
如果轮训结束没有发现**elected master Node**,该节点会停顿**discovery.find_peers_interval**秒后，再次轮训

### 选举主节点
**首先我们了解一下一些常用的分布式算法**  
es7.x之前，使用的是Bully算法，es7.x以后，改为raft算法。接下来分别理解下这两个算法  
##### bully算法
**概述**  
Bully算法是一种霸道的集群选主算法，选举原则是“长者”为大，即在所有活着的节点中，选取ID最大的节点为主节点。
**前置条件**  
集群中的每个节点均知道其他节点的ID
**选举过程**   
* 初始化时，网络环境正常，节点也无故障的情况下，集群中的每个节点都会判断自己的ID是否是当前活着的所有节点ID的最大的，如果是，则直接向其他节点发送Victory消息，宣誓自己为Leader
* 当主节点发生故障或其他原因导致重新选主时，如果当前节点发现自己的ID不是当前活着的节点中ID最大的，则向比自己ID大的所有节点发送Election消息，并等待回复Alive消息。
* 在给定的时间范围内，本节点如果没有收到其他节点回复的Alive消息，则认为自己成为Leader，并且向其他节点发送Victory消息。  如果接受到比自己ID大的节点的Alive消息，则人家比你大，老老实实的等待Victory消息吧。
**缺陷**  
* 主节点假死，其他节点重新选举主节点，当主节点回归时，会重新变为主节点。 es的解决方案是推迟重新选举，flower节点发现连接不上主节点时，会先Q其他节点，所有节点确认主节点跪了，再重新选举
* 脑裂。 es解决方案是：引入quorum算法。要求节点数为奇数，只有当获得投票数超过（n+1）/2时，才能作为主节点。这个数值是可以设置的  
**参考**  
[参考文章](https://codeantenna.com/a/JK5ADwEeY1)  
![备选地址](./bully/readme.html)

##### raft算法
* Raft是一种用于替代Paxos的共识算法。相比于Paxos，Raft的目标是提供更清晰的逻辑分工使得算法本身能被更好地理解，同时它安全性更高，并能提供一些额外的特性。[1][2]:1Raft能为在计算机集群之间部署有限状态机提供一种通用方法，并确保集群内的任意节点在某种状态转换上保持一致。  
* 集群内的节点都对选举出的领袖采取信任，因此Raft不是一种拜占庭容错算法  
* 在Raft集群（英语：Raft cluster）里，服务器可能会是这三种身份其中一个  
    * leader: 所有Candidate共同票选的领导者，接受客户端请求，并向Follower同步请求日志，当日志同步到大多数节点上后告诉Follower提交日志。通常领袖会借由固定时间发送消息，也就是心跳（heartbeat），让追随者知道集群的领袖还在运作
    * Follower: 接受并持久化Leader同步的日志，在Leader告之日志可以提交之后，提交日志。每个追随者都会设计超时机制（timeout），当超过一定时间没有收到心跳（通常是150 ms或300ms），集群就会进入选举状态，该超时Follower转换为Candidate
    * Candidate: Leader选举过程中的临时角色。也可称之为master-eligible，当Follower在一个定时器超时未收到leader心跳时，升级而来。
* Raft将问题拆成数个子问题分开解决
    * 领袖选举（Leader Election）
    * 日志同步（Log Replication）
    * 安全性（Safety）
      
* 参考
    * [知乎大佬](https://zhuanlan.zhihu.com/p/32052223)
    * [官方](http://thesecretlivesofdata.com/raft/)    

##### es的实现
* 每个节点在启动之前都可以设置属性：network.host: XXX.XXX.XXX.XX
* 把**master-eligible Node**的**network.host** 设置在每个节点的**discovery.seed_hosts**列表中
* 有了前两点，es集群中的每个节点都可以知道所有的**master-eligible Node**，每个节点内都有个定时器，在定时器结束时，它会向其他所有节点发起投票请求，
``` yaml
discovery.seed_hosts:
   - 192.168.1.10:9300
   - 192.168.1.11 
   - seeds.mydomain.com 
   - [0:0:0:0:0:ffff:c0a8:10c]:9301 
```
支持IPV6和域名，域名会进行DNS解析，支持多个IP。端口默认为**transport.profiles.default.port**属性，若该属性未配置，则为9300


### 健康检测


### 故障检测



