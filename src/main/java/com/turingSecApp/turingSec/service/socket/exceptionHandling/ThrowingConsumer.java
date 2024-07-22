package com.turingSecApp.turingSec.service.socket.exceptionHandling;

@FunctionalInterface
public interface ThrowingConsumer<T> {
    void accept(T t) throws Exception;
}

