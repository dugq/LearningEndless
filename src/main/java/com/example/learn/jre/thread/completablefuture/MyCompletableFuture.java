package com.example.learn.jre.thread.completablefuture;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Function;

public class MyCompletableFuture<T> {

    private volatile Object result;

    private volatile List<MyCompletableFuture<?>> next = new LinkedList<>();

    private ExecutorService executorService = new ThreadPoolExecutor(1,1,5, TimeUnit.DAYS,new ArrayBlockingQueue<>(16));

    private static final Nil nil = new Nil();

    static class Nil{}

    MyCompletableFuture(T result){
        if (Objects.isNull(result)){
            this.result = nil;
        }
        this.result = result;
    }

    public <V> MyCompletableFuture<V> thenHandler(Function<T,V> function){
        MyCompletableFuture<V> next = new MyCompletableFuture<>(null);
        this.next.add(next);

        return next;
    }

}
