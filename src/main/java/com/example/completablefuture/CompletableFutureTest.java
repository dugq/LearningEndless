package com.example.completablefuture;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * Created by dugq on 2020-05-07.
 */
public class CompletableFutureTest {
    @Resource
    private ExecutorService executorService;

    public void test(){
        CompletableFuture completableFuture = CompletableFuture.runAsync(()->{

        },executorService).toCompletableFuture();

    }
}
