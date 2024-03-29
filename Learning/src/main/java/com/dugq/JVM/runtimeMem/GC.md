
### 什么是垃圾回收(what)
* 自动化管理内存区域，包括对象内存的分配 和 无用对象的回收

### 为什么要垃圾回收(why)
* 高级语言的特性，自动化对象的内存分配和回收
* 为java应用的可持续运行提供基础保障

### 如何实现（how）
#### 1. 算法-如何找寻垃圾对象（标记）
###### 根节点枚举法
###### 跨代（区域）引用
#### 2. 算法-如何回收圾对象（回收）
#### 3. 如何分配内存

### 常用的垃圾回收器对比

| GC                  | 回收区域 | 回收算法 | 线程分类 | 优势          |   |   |
|---------------------|------|------|------|-------------|---|---|
| serial              | 新生代  | 标记复制 | 串行   | 单CPU小内存表现良好 |   |   |
| serial old          | 老年代  | 标记整理 | 串行   | 单CPU小内存     |   |   |
| parNew(serial的并行版本) | 新生代  | 标记复制 | 并行   | 多CPU小内存     |   |   |
| parallel scavenge   | 新生代  | 标记复制 | 并行   | 吞吐量优先       |   |   |
| parallel old        | 老年代  | 标记整理 | 并行   | 吞吐量优先       |   |   |
| cms                 |      |      |      |             |   |   |
| G1                  |      |      |      |             |   |   |

![img.png](../resource/cms线程模型.png)

| GC                           | 目标                | 算法 | 过程    | 线程模型      | 场景 |
|------------------------------|-------------------|----|-------|-----------|----|
| serial + serial old          |                   |    | 串行    | 单CPU小内存场景 |
| parallel + parallel scavenge | 可控制的吞吐量，满足程序的运行时长 |    | 并行    | 吞吐量优先     |
| parNew + cms                 | 低延迟，满足程序的响应速度     |    | 并行+并发 | 停顿时间优先    |
| G1                           |                   |    | 并发    | 大内存       |

##### 主要目标
###### serial + serial old

###### par new + parallel scavenge

###### par new + cms
* 获取最短回收停顿时间

###### G1
* 为了适应现在不断扩大的内存和不断增加的处理器数量
* 进一步降低暂停时间（pause time）并且让停顿时长可控
* 同时兼顾良好的吞吐量

### 工作区域
* CMS 主要针对老年代进行回收，一般和pallNew搭配使用
* G1 工作区域包括新生代和老年代

### 回收算法
###### serial + serial old

###### par new + parallel scavenge

###### par new + cms

###### G1
* CMS 
  * 优先采用标记-清理
  * 当碎片比例达到一定程度时，使用标记-整理
* G1
  * 整体对区域进行标记-整理
  * 区域内标记-复制

### 内存结构
###### serial + serial old

###### par new + parallel scavenge

###### par new + cms

###### G1
![](../resource/g1Mem.png)

### 回收过程
###### serial + serial old

###### par new + parallel scavenge

###### par new + cms

###### G1

### 使用场景
###### serial + serial old

###### par new + parallel scavenge

###### par new + cms

###### G1

### 综合评估
* G1将内存划分为多块，分别进行回收，所以内存上看
  * 在大内存场景下，通过回收特定数量的区域来降低单次停顿的时长，但总时长依然要比CMS高
  * 在小内存场景下，G1的区域回收反而成为浪费时间和空间的产品
* 回收过程上看
  * 对于停顿时长有要求的场景下，G1更具优势，它可以控制单次回收的时长。
  * 回收效率比较好的场景下，由于G1是标记复制算法，显然造成内存碎片化的要比CMS的标记清理要好。
