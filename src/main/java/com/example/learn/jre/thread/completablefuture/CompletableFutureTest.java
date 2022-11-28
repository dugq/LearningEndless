package com.example.learn.jre.thread.completablefuture;

import com.example.util.ThreadUtil;
import org.junit.Test;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by dugq on 2020-05-07.
 */
public class CompletableFutureTest {

    private ExecutorService executorService = Executors.newFixedThreadPool(3);


    /**
     * {@link CompletionStage}
     */
    public interface CompletionStageTest<T>{

        /**
         * 在已有的一个阶段任务 追加一个任务
         */
        public <U> CompletionStage<U> thenApply(Function<? super T,? extends U> fn);
        public <U> CompletionStage<U> thenApplyAsync(Function<? super T,? extends U> fn);
        public <U> CompletionStage<U> thenApplyAsync(Function<? super T,? extends U> fn, Executor executor);

        public CompletionStage<Void> thenAccept(Consumer<? super T> action);
        public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action);
        public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor);

        public CompletionStage<Void> thenRun(Runnable action);
        public CompletionStage<Void> thenRunAsync(Runnable action);
        public CompletionStage<Void> thenRunAsync(Runnable action, Executor executor);

        public <U> CompletionStage<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn);
        public <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn);
        public <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn, Executor executor);

        public CompletionStage<T> whenComplete(BiConsumer<? super T, ? super Throwable> action);
        public CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action);
        public CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor);

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

    public void completableFuture() throws ExecutionException, InterruptedException {


        /**
         * 没有 *Async结尾的任务定义不会更换线程执行，继续使用上一个阶段任务使用的线程，有Async结尾的任务会更换线程。{@link #testThread()}
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
    }

    @Test
    public void testHandler(){
        CompletableFuture<Integer> future = CompletableFuture.completedFuture(1).handle((pre, ex) -> null).handleAsync((pre, ex) -> 1);
        System.out.println(future.join());
    }

    @Test
    public void testThread() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> supply = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println("start  "+Thread.currentThread().getId());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("supply "+Thread.currentThread().getId());
                    throw new RuntimeException("fasf");
                },executorService)
                .handle((p, e) -> {
                    if (Objects.isNull(p) && Objects.nonNull(e)){
                        System.out.println("handler "+e.getMessage()+"  "+Thread.currentThread().getId());
                    }
                    return 2;
                })
                .thenAcceptAsync((p)-> System.out.println("accept :"+Thread.currentThread().getId()),executorService);
        System.out.println("main "+Thread.currentThread().getId());
        System.out.println(supply.get());
    }

    @Test
    public void testThread2(){
        CompletableFuture.completedFuture(null).handle((pre,ex)->{
            System.out.println("handle 1 threadId = "+Thread.currentThread().getId());
            return 1;
        }).handleAsync((pre,ex)->{
            System.out.println("handle 2 threadId = "+Thread.currentThread().getId());
            return 2;
        });
        System.out.println("main threadId = "+Thread.currentThread().getId());
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
}
