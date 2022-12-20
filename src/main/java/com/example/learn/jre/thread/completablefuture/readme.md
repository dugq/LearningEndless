## Future
* Future表示异步计算的结果。
* 方法用于检查计算是否完成、等待计算完成以及检索计算结果。
* 只能在计算完成时使用get方法检索结果，如果需要则阻塞，直到它准备好。
* 取消是由cancel方法执行的。还提供了其他方法来确定任务是正常完成还是被取消。 一旦计算完成，计算就不能取消。

## CompletionStage
* 阶段抽象类
* 定义了阶段任务的每个阶段模版方法
* 简单的解释就是  火车头式函数调用的模版方法，并且支持 runnable consumer function Supplier 等不同方式，每种方式又提供了另起线程执行的方法
* [测试类](CompletableFutureTest.java)


## CompletableFuture
* 同时实现了Future 和 CompletionStage，实现一次性提交多个有序的按阶段执行任务的异步任务，并返回一个CompletableFuture异步获取结果
* [基本用法示例](CompletableFutureTest.java)

### 实现细节
#### 私有属性
#####  volatile Object result;
* 该字段存储异步计算结果，类型不一定是CompletableFuture声明的类型，也可能是AltResult封装的null对象，或者是各类异常（包括取消异常和各种任务抛出的异常）
* 字段用volatile修饰，以便get等方法快速获取到任务的结果
* result只有在任务初始化的时候为null，一旦任务完成，它将会被赋予任务的结果（可能是某个异常，AltResult 或者任务的实际结果）。
#####  volatile Completion stack;
* 该字段用于触发依赖的任务，记录依赖当前结果的任务栈。类型为Completion
* 同样用volatile修饰
* 追加依赖当前任务的任务时，保证追加的任务一定会执行，必须分三步走
  * 1、判断当前任务result!=null时，调用追加任务的tryFire方法，尝试直接触发任务的执行。如果result==null说明当前任务未结束，继续下一步
  * 2、调用 push 方法，将新的依赖当前任务的任务到stack中。push依赖的是tryPushStack方法。
    * tryPushStack 是一个死循环的操作，只要result不等于null(说明当前任务未结束),或者push失败，就会继续进行。
    * 注意 tryPushStack 不是一个原子性，且没有事物性，所以在 tryPushStack 失败的时候，stack也会变更。所以push方法中，在tryPushStack 失败的时候，需要重置新任务的依赖栈
  * 3、再调用一次新任务的tryFire尝试开始任务，以防止在push结束的同时，当前任务也结束，并且stack的遍历已经get到依赖任务栈的栈顶元素，或者是push的过程中当前任务已经结束，导致push失败。

#### 内部抽象类 Completion.java
* Completion 继承了ForkJoinTask 利用ForkJoinTask的 compareAndSetForkJoinTaskTag 方法来保证任务的非重复执行
* Completion 实现AsynchronousCompletionTask.class 用以标记归类
* Completion 实现了Runnable 用以将任务提交线程执行
* CompletableFuture 实现了各类CompletionStage方法，每种类型的任务以不同的Completion 子类去是实现
* Completion 的tryFire 方法用以启动任务，支持：同步、异步、内嵌三种方式。
* Completion 包含next属性，用以实现堆栈逻辑
  * UniCompletion
    * 属性
      * Executor executor 保存执行任务的线程池
      * CompletableFuture<V> dep  当前任务的CompletableFuture对象，在任务执行完成时更新result的值，以及触发其依赖栈 stack的任务
      * CompletableFuture<T> src  上一个任务，可理解为被依赖的任务
    * 方法
      * claim()，当需要触发时调用此方法，内部使用executor 异步调用tryFire方法，返回false 表示执行成功或者已经被执行，返回true，表示有任务可以执行，但没有线程去执行
      * isLive()，判断当前任务是否可以正常被触发。判断持有任务的CompletableFuture是否存在为依据
      * tryFire()，抽象方法，各子类根据任务类型的不同，执行不同逻辑，但是大致步骤一致
          * 1、判断结果result==null，继续执行，如果非空说明已经执行过了，跳过
          * 2、判断依赖的CompletableFuture是否有异常，如果有异常且当前任务不接收exception时，直接修改result为依赖CompletableFuture的异常,其他情况构建依赖Completable的结果和exception继续
          * 3、如果tryFire的参数是同步(SYNC)，直接执行任务， 如果是异步或者嵌套则，调用claim方法尝试修改标记，并异步调用tryFire，以实现异步执行任务。如果方法返回true，则直接执行任务(使用当前线程继续执行)，返回false说明有指定线程池，claim方法会实现异步，当前方法return null; 表示任务还在进行中
            * 这里注意： claim异步执行使用的是tryFire，tryFire 又调用了claim以实现异步，看上去会递归，实际不会。
            * 因为claim内部借用父类 ForkJoinTask的compareAndSetForkJoinTaskTag方法阻断了线程的继续进行。
          * 4、同步或者异步执行完任务后，会尝试将结果或者异常存入result中，不管结果有没有成功，如果CompletableFuture被取消或者其他原因导致结果有值的时候，任务虽然执行完了，但结果不会变更的
          * 5、然后将所有的属性引用置空。看上去没啥必要。这里就是jdk作为底层API想的比较多了。这里是为了避免在任务队列很长且比较占用内存时，导致所有引用链持续存在，导致内存溢出。
          * 6、最后 Completion 作为任务者的使命都完成了，所以调用CompletableFuture的postFire方法，将球交给还给CompletableFuture对象
    * 列举子类
      * UniApply 
        * 属性 ： Function<? super T,? extends V> fn;  
        * 执行： src.result.ex != null ? dp.result=src.result.ex  :  dp.result=fn.apply(src.result);  
      * UniWhenComplete 
        * 属性： BiConsumer<? super T, ? super Throwable> fn; 
        * 执行： fn.apply(src.result,src.result.ex); dp.result=src.result;
      * UniCompose 
        * 属性：Function<? super T, ? extends CompletionStage<V>> fn; 
        * 执行： dp.result = fn.apply(src.result).toCompletableFuture().result;(这里依赖UniRelay实现)
      * UniExceptionally 
        * 属性 Function<? super Throwable, ? extends T> fn;  
        * 执行： dp.result = src.result==AltResult?fn.apply(src.result.ex):src.result;
      * UniAccept 
        * 属性： Consumer<? super T> fn; 
        * 执行：  src.result.ex != null ? (dp.result = src.result.ex ) : (fn.accept(src.result); dp.result = Nil)
      * UniRun 
        * 属性： Runnable fn;
        * 执行： src.result.ex == null ? (dp.result = src.result.ex ) : (fn.run(); dp.result = Nil)
      * UniHandle
        * 属性： BiFunction<? super T, Throwable, ? extends V> fn;
        * 执行： dp.result = fn.apply(src.result,src.result.ex)
      * UniRelay
        * 属性：无
        * 执行：dp.result = src.result;
      * BiCompletion
        * 属性： CompletableFuture\<U> snd;
        * 执行：不实现tryFire方法，交给子类去实现
          * OrRun
            * 属性：Runnable fn;
            * 执行： src.result.ex!=null || snd.result.ex!=null ? dp.result=ex : (fn.run(); dp.result=Nil)
          * OrRelay
            * src.result != null ? dp.result = src.result : dp.result = snd.result
          * OrAccept
          * OrApply
          * BiAccept
            * 属性： BiConsumer<? super T,? super U> fn;
            * 执行： (src || snd).result.ex != null ? dp.result = ex : fn.accept(src.result,snd.result); dp.result = Nil
          * BiApply 
          * BiRun
          * BiRelay
            * 执行 src.result.ex!=null || snd.result.ex!=null ? dp.result=ex :  dp.result=Nil
  * CoCompletion
    * BiCompletion 的代理类。目前用在组合CompletableFuture对象时，给第二个依赖追加依赖栈时使用。难道避免两个CompletableFuture引用了同一个对象，从而造成假死或者死循环？      
  * Signaller 为了实现get / join / getNow 等方法的任务 后续再细细研究
    * tryFire 仅仅做了线程解锁的动作
    * 额外实现了 ForkJoinPool.ManagedBlocker 。再研究

##### 关键方法
* tryFire
  * 1、判断当前Future是否执行完毕，如已经完毕，则返回null
  * 2、调用具体的Complete进行具体的执行
  * 3、调用Complete父类方法claim获取并发标记(ForkJoinTask#compareAndSetForkJoinTaskTag)
  * 4、如果任务设置了异步线程，则唤起异步线程，开始执行。同时返回null
  * 5、如果是同步或者内嵌模式，直接执行
  * 6、4和5在任务执行完成以后都会将Complete的所有属性置空，以保证在递归未执行完时，无用的引用可以及时回收
  * 7、调用postFire
* postFire
  * 1、处理源Future的stack
    * 如果当前是内嵌模式或者源Source未执行（result==null）则清理掉sourceFuture的stack中不可执行或者依旧执行过的依赖（调用Source future的cleanStack方法）
    * 如果不是内嵌模式，则调用source future的PostComplete方法。（帮助源Future分担压力。如果说源是第0级，当前Future是第1级，那么依赖当前Future的Future就是第2级了，优先帮助第0级处理任务，这也是应该的。同时如果没有第2级，那也不浪费当前线程）
  * 2、处理当前Future的依赖栈
    * 如果当前是内嵌模式，则把当前Future返回。postComplete会处理内嵌模式的依赖栈。
    * 如果当前不是内嵌模式，则调用当前Future的PostComplete方法进行依赖栈处理
  * 为什么postFire还要关心源Future的stack呢？
    * 1、postComplete不仅仅只处理自己的依赖栈，它也处理了依赖当前Future且是同步执行的依赖栈（即为内嵌模式），所以要分模式选择是否自己处理依赖栈
    * 2、为什么要有1呢？因为这样就可以将所有同步执行的依赖栈放在同一栈中，在异步模式的PostFire中，可以帮助源Future一起处理依赖栈
    * 3、多线程同时执行依赖栈时，怎么保证一致性呢？stack节点的出入是原子性的，这样就保证多线程不会处理同一个节点。[测试类](CompletableFutureTest.java)testPostFireAndComplete2方法可窥见一般
* postComplete
  * 1、轮训stack
  * 2、以内嵌模式调用每个节点的tryFire
  * 3、将返回的Future的所有节点退出并push进当前stack中。
    * 如果节点任务为异步的，那就不用源Future帮忙处理了。直接返回null
    * 这样其实导致stack是乱的。[测试类](CompletableFutureTest.java)testPostFireAndComplete方法可窥见一般
  * 4、当栈为空时，结束
* get/join 的阻塞
  * 
* 任务中断 cancel 方法
  * 
* 等待中断 get方法
  * 

#### 线程安全
* 首先任务在Future中定义一个Completion作为节点，存放在stack中，对于stack的所有操作都利用了Unsafe类实现了原子性
* 任务的执行中，利用继承自ForkJoin的compareAndSetForkJoinTaskTag方法实现了原子锁

