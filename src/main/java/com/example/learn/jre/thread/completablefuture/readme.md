## Future
* Future表示异步计算的结果。
* 方法用于检查计算是否完成、等待计算完成以及检索计算结果。
* 只能在计算完成时使用get方法检索结果，如果需要则阻塞，直到它准备好。
取消是由cancel方法执行的。还提供了其他方法来确定任务是正常完成还是被取消。 一旦计算完成，计算就不能取消。

## CompletionStage
* 阶段抽象类
* 定义了阶段任务的每个阶段模版方法
* 简单的解释就是  火车头式函数调用的模版方法，并且支持 runnable consumer function Supplier 等不同方式，每种方式又提供了另起线程执行的方法


## CompletableFuture
* 同时实现了Future 和 CompletionStage，实现一次性提交多个有序的按阶段执行任务的异步任务，并返回一个CompletableFuture异步获取结果
* [基本用法示例](CompletableFutureTest.java)

### 实现细节
#### 异步

#### 任务中断 cancel 方法

#### 等待中断 get方法

#### 线程安全

#### get/join 如何做到获取到最终结果，而不是中间结果