package com.example.learn.jreApi.thread.completablefuture;

import com.example.util.ThreadUtil;
import org.junit.Test;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * Created by dugq on 2020-05-07.
 */
public class CompletableFutureTest {

    /**
     * {@link CompletionStage}
     */
    public interface CompletionStageTest<T>{

        /**
         * 在已有的一个阶段任务 追加一个任务
         */
        //一个参数 一个结果 function
        public <U> CompletionStage<U> thenApply(Function<? super T,? extends U> fn);
        public <U> CompletionStage<U> thenApplyAsync(Function<? super T,? extends U> fn);
        public <U> CompletionStage<U> thenApplyAsync(Function<? super T,? extends U> fn, Executor executor);
        //一个参数 没有结果 consumer
        public CompletionStage<Void> thenAccept(Consumer<? super T> action);
        public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action);
        public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor);
        //没有参数 没有结果 runnable
        public CompletionStage<Void> thenRun(Runnable action);
        public CompletionStage<Void> thenRunAsync(Runnable action);
        public CompletionStage<Void> thenRunAsync(Runnable action, Executor executor);
        //一个参数，一个结果 和function的区别在于 function的入参是结果，而compose直接是Completion对象
        public <U> CompletionStage<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn);
        public <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn);
        public <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn, Executor executor);
        //两个参数，没有结果 和 consumer的区别在于，额外接收exception，如果发送异常了，还是会执行下个任务
        public CompletionStage<T> whenComplete(BiConsumer<? super T, ? super Throwable> action);
        public CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action);
        public CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor);
        //两个参数，一个结果 和function的区别在于额外接收exception，如果发送异常了，还是会执行下个任务
        public <U> CompletionStage<U> handle(BiFunction<? super T, Throwable, ? extends U> fn);
        public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn);
        public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn, Executor executor);


        /**
         * 在已有的两个前置阶段任务后的第三个阶段任务
         */
        public <U,V> CompletionStage<V> thenCombine(CompletionStage<? extends U> other, BiFunction<? super T,? super U,? extends V> fn);
        public <U,V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T,? super U,? extends V> fn);
        public <U,V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T,? super U,? extends V> fn, Executor executor);

        public <U> CompletionStage<Void> thenAcceptBoth(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action);
        public <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action);
        public <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action, Executor executor);

        public CompletionStage<Void> runAfterBoth(CompletionStage<?> other, Runnable action);
        public CompletionStage<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action);
        public CompletionStage<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor);

        public <U> CompletionStage<U> applyToEither(CompletionStage<? extends T> other, Function<? super T, U> fn);
        public <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn);
        public <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn, Executor executor);

        public CompletionStage<Void> acceptEither(CompletionStage<? extends T> other, Consumer<? super T> action);
        public CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action);
        public CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action, Executor executor);

        public CompletionStage<Void> runAfterEither(CompletionStage<?> other, Runnable action);
        public CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action);
        public CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action, Executor executor);

        /**
         * 异常处理阶段任务
         */
        public CompletionStage<T> exceptionally(Function<Throwable, ? extends T> fn);

        /**
         * 自定义的CompletionStage 需要兼容官方的
         */
        public CompletableFuture<T> toCompletableFuture();
    }

    /**
     * Future 表示一个异步计算的结果
     * jdk 实现类包括 {@link CompletableFuture} {@link ForkJoinTask} {@link RunnableFuture}
     */
    public interface futureTest<V>{
        /**
         * 尝试取消任务
         *  若任务已完成，则无法取消，此方法返回false
         *  如果任务已经开始但未结束，则根据入参决定是否进行中断尝试，并将结果标记为已取消
         *  如果任务未开始，则将会被直接结束
         */
        boolean cancel(boolean mayInterruptIfRunning);
        boolean isCancelled();

        /**
         * 取消也算在完成之内
         */
        boolean isDone();

        /**
         * 获取任务结果
         * 如果任务尚在进行中，当前线程将会被阻塞，但当前阻塞可被中断。
         */
        V get() throws InterruptedException, ExecutionException;
        V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;
    }

    public abstract static class CompletableFutureMethodTest<T> implements Future<T>,CompletionStage<T>{

        /**
         *  提供了5个静态方法，以直接开始阶段任务的首个任务
         */
        public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier) {return null;}
        public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor){return null;};
        public static CompletableFuture<Void> runAsync(Runnable runnable) {return null;}
        public static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor) {return null;}
        public static <U> CompletableFuture<U> completedFuture(U value) { return null;}

        /**
         * 提供对结果获取的快捷方法
         */
        public T join() {return null;}
        public T getNow(T valueIfAbsent) {return null;}
        public boolean isCompletedExceptionally() {return false;}

        /**
         * 提供对结果操作的额外方法
         */
        public boolean complete(Object value) {return true;}
        public boolean completeExceptionally(Throwable ex) {return true;}
        public void obtrudeValue(T value) {}
        public void obtrudeException(Throwable ex) {}

        /**
         * 监控类
         */
        public int getNumberOfDependents() {return 0;}


        public static CompletableFuture<Void> allOf(CompletableFuture<?>... cfs) {return null;}
        public static CompletableFuture<Object> anyOf(CompletableFuture<?>... cfs) {return null;}
    }

    public void completableFuture() throws ExecutionException, InterruptedException {


        /**
         * 没有 *Async结尾的任务定义不会更换线程执行，继续使用上一个阶段任务使用的线程，有Async结尾的任务会更换线程(线程来自传入的ThreadPool，如果没有传入，则使用{@link ForkJoinPool#commonPool})。{@link #testThread()}
         * 如第一个则使用主线程{@link #testThread2}
         *
         */
        CompletableFuture<Integer> future = CompletableFuture.completedFuture(1).handle((pre,ex)->pre).handleAsync((pre, ex) -> pre + 1);

        /**
         * 定义的是表示最后一个任务的结果，泛型以最后一个为准
         * {@link #testGenerics}
         */
        CompletableFuture<String> future2 = CompletableFuture.completedFuture(1).thenRun(()-> System.out.println("run 1")).handle((pre,ex)->1).thenRun(()-> System.out.println("run 2")).handle((pre,ex)->"");

        /**
         * 获取值，当前线程阻塞在此处，可被Thread.interrupt  {@link #testGetInterrupt}
         * 需要抓获异常：
         *    ExecutionException ： 任务报错
         *    InterruptedException： 当前等待结果的线程被嘎了
         */
        future.get();

        // 获取值，若任务没有结果，返回入参值作为默认值。当前线程不会阻塞，也不会影响任务的进行
        future.getNow(3);
        /**
         * 取消任务，并根据入参决定是否中断进行中的任务 {@link #testCancelInterrupt}
         * 任务结果被强行赋予 CancellationException
         * 任务的取消在每个阶段完成后，检测到已被中断，不再继续下个阶段，不会强行嘎调线程，也不会尝试 interrupt
         * {@link #testCancelSource}
         */
        future.cancel(true);

        /**
         * 判读任务是否完成
         * 这只对于有返回值的future，有用。{@link #testDone2}
         * 没有返回值，done 始终是false {@link #testDone}
         * 多个任务时 所有任务执行完才会返回true {@link #testDone3}
         */
        future.isDone();

        /**
         * 等待完成后返回返回值。和get()的区别在于，这里就是死等
         *  {@link #testGetInterrupt}
         */
        future.join();
    }

    @Test
    public void testGenerics() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future2 = CompletableFuture.completedFuture(1).thenRun(()-> System.out.println("run 1")).handle((pre,ex)->1).thenRun(()-> System.out.println("run 2")).handle((pre,ex)->"");
        System.out.println(future2.get());

        // null 也会继续执行
        CompletableFuture<Void> future3 = CompletableFuture.completedFuture(null);
        future3.thenAccept(a->{});
    }

    @Test
    public void testHandler(){
        CompletableFuture<Integer> future = CompletableFuture.completedFuture(1).handle((pre, ex) -> null).handleAsync((pre, ex) -> 1);
        System.out.println(future.join());
    }

    @Test
    public void testThread() throws ExecutionException, InterruptedException {
        ExecutorService threadPool = ThreadUtil.getThreadPool("test-one");
        CompletableFuture<Void> supply = CompletableFuture
                .supplyAsync(() -> {
                    ThreadUtil.printThreadInfo("start");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    ThreadUtil.printThreadInfo("supply");
                    throw new RuntimeException("fasf");
                }, threadPool)
                .handle((p, e) -> {
                    ThreadUtil.printThreadInfo("handler");
                    if (Objects.isNull(p) && Objects.nonNull(e)){
                        System.out.println("handler "+e.getMessage()+"  ");
                    }
                    return 2;
                })
                .thenAcceptAsync((p)-> ThreadUtil.printThreadInfo("accept"),ThreadUtil.getThreadPool("test-two"));
        ThreadUtil.printThreadInfo("main");
        System.out.println(supply.get());
    }

    @Test
    public void testThread2(){
        CompletableFuture.completedFuture(null).thenRunAsync(()->{}).handle((pre,ex)->{
            ThreadUtil.printThreadInfo("handle 1");
            return 1;
        }).handleAsync((pre,ex)->{
            ThreadUtil.printThreadInfo("handle 2");
            return 2;
        });
        ThreadUtil.printThreadInfo("main");
    }

    @Test
    public void testGetInterrupt() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future = CompletableFuture.completedFuture(1)
                .handleAsync((pre, ex) -> {System.out.println("handle 1 id=" +Thread.currentThread().getId());ThreadUtil.sleep(10);return pre + 1;});

        Thread thread1 = new Thread(() -> {
            try {
                System.out.println("thread1 get = " + future.get());
            } catch (InterruptedException e) {
                System.out.println("thread1 get " +"interrupted");
            } catch (ExecutionException e) {
                System.out.println("thread1 get " +"exception");
            }catch (CancellationException e){
                System.out.println("thread1 get canceled");
            }
        });
        thread1.start();
        Thread thread2 = new Thread(() -> {
            try {
                System.out.println("thread 2 join = "+future.join());
            }catch (Exception e){
                System.out.println("thread2 canceled");
            }
        });
        thread2.start();
        ThreadUtil.sleep(5);
        System.out.println("try interrupt !");
        thread1.interrupt();
        thread2.interrupt();
        System.out.println("task over "+future.get());
        ThreadUtil.sleep(3);
    }


    @Test
    public void testCancelInterrupt()  {
        CompletableFuture<Integer> future = CompletableFuture.completedFuture(1)
                .handleAsync((pre, ex) -> {System.out.println("handle 1 id=" +Thread.currentThread().getId());ThreadUtil.interrupt("handle 1");return pre + 1;})
                .handleAsync((pre, ex) -> {ThreadUtil.sleep(1);System.out.println("handle 2 id="+ Thread.currentThread().getId());ThreadUtil.interrupt("handle 2");return pre + 1;})
                .handleAsync((pre, ex) -> {System.out.println("handle 3 pre id="+ Thread.currentThread().getId());ThreadUtil.sleep(5);System.out.println("post handle 3 id="+ Thread.currentThread().getId());ThreadUtil.interrupt("handle 3");return pre + 1;})
                .handleAsync((pre, ex) -> {ThreadUtil.sleep(1);System.out.println("handle 4 id="+ Thread.currentThread().getId());ThreadUtil.interrupt("handle 4"); return pre + 1;});
        ThreadUtil.sleep(3);
        //3s后  handler 3 已经开始，此时中止任务，handler3 仍会继续，在若干秒后，post handle 3 会继续输出
        System.out.println("cancel = "+future.cancel(false));
        try {
            //get 必须捕获InterruptedException 和 ExecutionException。而CancellationException 也应该捕获，如果需求有cancel的情况下。否则，代码到这就throw RuntimeException了
            System.out.println("over " + future.get());
        } catch (InterruptedException e) {
            System.out.println("over interrupt");
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }catch (CancellationException e){
            System.out.println("over canceled");
        }
        //继续等待handler 3 的完成
        ThreadUtil.sleep(10);
    }

   @Test
    public void testDone(){
       CompletableFuture<Void> future = new CompletableFuture<>().thenRunAsync(()->ThreadUtil.sleep(5));
       //未初始化带有result的CompletableFuture对象并且未设定用带有返回值的任务时，isDone is always false
       System.out.println(future.isDone());
       ThreadUtil.sleep(8);
       System.out.println(future.isDone());
   }

    @Test
    public void testDone2(){
        CompletableFuture<Void> future =CompletableFuture.completedFuture(1).thenRunAsync(()->ThreadUtil.sleep(5));
        //初始化带有result的CompletableFuture对象后，isDone is always true
        System.out.println(future.isDone());
        ThreadUtil.sleep(8);
        System.out.println(future.isDone());
    }

    @Test
    public void testDone3(){
        CompletableFuture<Void> future =CompletableFuture.completedFuture(null).thenRunAsync(()->{
            ThreadUtil.sleep(5);
            System.out.println("run 1 complete");
        }).thenRun(()->{
            ThreadUtil.sleep(5);
            System.out.println("run 2 complete");
        });
        //初始化带有result的CompletableFuture对象后，isDone is always true
        System.out.println(future.isDone());
        ThreadUtil.sleep(8);
        System.out.println(future.isDone());
        ThreadUtil.sleep(5);
        System.out.println(future.isDone());
    }

    @Test
    public void testCancelSource(){
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> ThreadUtil.sleep(11)).thenRun(() -> ThreadUtil.sleep(1));
        future.cancel(true);
    }

    @Test
    public void testStack(){
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "fasdf");
        completableFuture.thenApply((param)->"fff");
    }

    @Test
    public void testPostFireAndComplete(){
        CompletableFuture<Void> A = CompletableFuture.runAsync(()->{ThreadUtil.sleep(3);System.out.println("A");});
        CompletableFuture<Void> A1 = A.thenRun(()-> System.out.println("A1"));
        CompletableFuture<Void> A2 = A.thenRun(()-> System.out.println("A2"));
        CompletableFuture<Void> A3 = A.thenRun(()-> System.out.println("A3"));

        CompletableFuture<Void> B1 = A1.thenRun(()-> System.out.println("B1"));
        CompletableFuture<Void> B2 = A1.thenRun(()-> System.out.println("B2"));
        CompletableFuture<Void> B3 = A1.thenRun(()-> System.out.println("B3"));

        CompletableFuture<Void> C1 = A2.thenRun(()-> System.out.println("C1"));
        CompletableFuture<Void> C2 = A2.thenRun(()-> System.out.println("C2"));
        CompletableFuture<Void> C3 = A2.thenRun(()-> System.out.println("C3"));

        CompletableFuture<Void> D1 = A3.thenRun(()-> System.out.println("D1"));
        CompletableFuture<Void> D2 = A3.thenRun(()-> System.out.println("D2"));
        CompletableFuture<Void> D3 = A3.thenRun(()-> System.out.println("D3"));
        ThreadUtil.sleep(6);
    }

    @Test
    public void testPostFireAndComplete2(){
        CompletableFuture<Void> A = CompletableFuture.runAsync(()->{ThreadUtil.sleep(3);System.out.println("A");},ThreadUtil.getThreadPool("ThreadA+"));
        A.thenRun(()-> {ThreadUtil.sleep(1);ThreadUtil.printThreadInfo("A1");});
        A.thenRun(()-> {ThreadUtil.sleep(1);ThreadUtil.printThreadInfo("A2");});
        A.thenRun(()-> {ThreadUtil.sleep(1);ThreadUtil.printThreadInfo("A3");});
        A.thenRun(()-> {ThreadUtil.sleep(1);ThreadUtil.printThreadInfo("A4");});
        A.thenRun(()-> {ThreadUtil.sleep(1);ThreadUtil.printThreadInfo("A5");});
        A.thenRun(()-> {ThreadUtil.sleep(1);ThreadUtil.printThreadInfo("A6");});
        A.thenRun(()-> {ThreadUtil.sleep(1);ThreadUtil.printThreadInfo("A7");});
        A.thenRun(()-> {ThreadUtil.sleep(1);ThreadUtil.printThreadInfo("A8");});
        A.thenRun(()-> {ThreadUtil.sleep(1);ThreadUtil.printThreadInfo("A9");});
        A.thenRun(()-> {ThreadUtil.sleep(1);ThreadUtil.printThreadInfo("A10");});
        A.thenRunAsync(()->{ThreadUtil.sleep(1);ThreadUtil.printThreadInfo("B1");},ThreadUtil.getThreadPool("ThreadB-"));

        ThreadUtil.sleep(600);
    }


    @Test
    public void testPostComInGet() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> A = CompletableFuture.runAsync(()->{ThreadUtil.sleep(30);System.out.println("A");},ThreadUtil.getThreadPool("ThreadA+"));
        A.thenRun(()-> {ThreadUtil.sleep(1);ThreadUtil.printThreadInfo("A1");});
        A.thenRun(()-> {ThreadUtil.sleep(1);ThreadUtil.printThreadInfo("A2");});
        A.thenRun(()-> {ThreadUtil.sleep(1);ThreadUtil.printThreadInfo("A3");});
        A.thenRun(()-> {ThreadUtil.sleep(1);ThreadUtil.printThreadInfo("A4");});
        A.thenRun(()-> {ThreadUtil.sleep(1);ThreadUtil.printThreadInfo("A5");});
        A.thenRun(()-> {ThreadUtil.sleep(1);ThreadUtil.printThreadInfo("A6");});
        A.thenRun(()-> {ThreadUtil.sleep(1);ThreadUtil.printThreadInfo("A7");});
        A.thenRun(()-> {ThreadUtil.sleep(1);ThreadUtil.printThreadInfo("A8");});
        A.thenRun(()-> {ThreadUtil.sleep(1);ThreadUtil.printThreadInfo("A9");});
        A.thenRun(()-> {ThreadUtil.sleep(1);ThreadUtil.printThreadInfo("A10");});
        System.out.println("A.get()="+A.get());
    }
}
