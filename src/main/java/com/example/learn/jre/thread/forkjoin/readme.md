* ForkJoinPool.ManagedBlocker

# Fork and Join
* Fork/Join框架是Java 7提供的一个用于并行执行任务的框架，是一个把大任务分割成若干 个小任务，最终汇总每个小任务结果后得到大任务结果的框架。
* [示例](./ForkJoinTaskTest.java)
* ![详解](./resource/fork-Join.png)

## 关键类
### ForkJoinTask
* RecursiveAction
* CountedCompleter
* RecursiveTask

### ForkJoinPool
* lockRunState 加锁方法


###### WorkQueue


###### ForkJoinWorkThread
