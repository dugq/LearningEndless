# ThreadPool
#### 线程池状态
![](../resource/threadPoolStatus.jpg)

#### 线程池线程创建参数列表
* corePoolSize                       核心线程数
* maximumPoolSize                    最大线程数
* keepAliveTime                      线程活跃时间
* TimeUnit unit                      活跃时间单位
* BlockingQueue<Runnable> workQueue  等待队列
* ThreadFactory threadFactory        线程构造器
* RejectedExecutionHandler handler   任务饱和时的处理策略


#### 线程池任务执行过程
![](../resource/threadPoolExecutor.jpeg)


#### execute Vs submit
* submit 返回Future，可通过Future获取任务进度


#### [阻塞队列](../../queue/readme.md)

### 任务饱和时的处理策略
