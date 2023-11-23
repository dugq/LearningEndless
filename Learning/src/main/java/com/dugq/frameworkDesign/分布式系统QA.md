* 客户端如何和集群建立联系？
* 
* 集群如何保持节点之间数据一致性？

|      | mysql主从 | redis集群 | zk集群 | rocketmq broker主从 | rocketmq name server |
|------|---------|---------|------|-------------------|----------------------|
| 同步策略 |         |         |      |                   |                      |

* 数据持久化策略
  * 如java，我们利用output将数据写入到文件流后，数据流不会立刻刷新到磁盘，而是停留在系统的page cache中
  * 当page cache满了，或者后台定时任务到点时，才会将page cache内的数据刷到磁盘中
  * 这样可以极大的减少文件的IO次数。（缓存->合并写）
  * 这样，在应用异常关闭时，数据流保留在操作系统内核缓冲区内，数据是有安全保障的。但是如何操作系统异常关机了，那缓存中的数据就丢失了
  * 而如何page cache未满时，最差需要30s才能被后台任务刷新到磁盘，这就极大了增加了数据丢失的风险。
  * 所以大部分需要持久化的系统大概率提供以下策略：
    * 1、自行启动一个时间间隔更短的定时任务，利用unix fsync函数刷新自己的文件
      * 中立策略。间隔短，异步刷新，不影响性能。但可能会丢失间隔时间内的数据
    * 2、同步调用fsync函数，强行将指定的文件所对应的数据流刷到磁盘中。
      * 安全策略。操作成功数据一定会刷到磁盘。但同步进行文件IO，性能最差。
        * 注意，即使同步写，也分为顺序写和随机写。其中顺序写性能要比随机写性能优很多。
    * 3、不管。采用unix操作系统策略。
      * 性能最好。但是安全性是最差的。

|          | mysql binlog       | innodb redo log                       | redis                     | rocketmq | zk |   |
|----------|--------------------|---------------------------------------|---------------------------|----------|----|---|
| 定时刷新     | sync_binlog=0 （每秒） | innodb_flush_log_at_trx_commit=0 （每秒） | appendfsync=EverySec （每秒） |          |    |   |
| 操作系统自动刷新 | sync_binlog=2      | innodb_flush_log_at_trx_commit=2      | appendfsync=NO            |          |    |   |
| 同步刷新     | sync_binlog=1      | innodb_flush_log_at_trx_commit=1      | appendfsync=Always        |          |    |   |
| 默认       | sync_binlog=1      | innodb_flush_log_at_trx_commit=1      | appendfsync=NO            |          |    |   |

appendfsync= 控制AOF日志写盘策略
*   同步写回
*  每秒写回
* No      依赖操作系统写回
