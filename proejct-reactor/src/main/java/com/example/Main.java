package com.example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Main {
    public static void main(String[] args) {
        Flux.just("hello word","123123")
                .map(String::toUpperCase)
                .doOnEach(System.out::println)
                .doOnNext(System.out::println)
                .subscribe();

    }
}