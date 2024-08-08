package com.turingSecApp.turingSec.exception.websocket.exceptionHandling;

@FunctionalInterface
public interface ThrowingConsumer<T> {
    void accept(T t) throws Exception;
}

