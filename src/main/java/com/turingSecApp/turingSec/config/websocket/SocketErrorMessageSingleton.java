package com.turingSecApp.turingSec.config.websocket;

import com.turingSecApp.turingSec.service.socket.exceptionHandling.SocketErrorMessage;

public class SocketErrorMessageSingleton {

    // Volatile keyword ensures that multiple threads handle the uniqueInstance variable correctly.
    private static volatile SocketErrorMessage instance;

    // Private constructor to prevent instantiation
    private SocketErrorMessageSingleton() {
        // Initialize the SocketErrorMessage instance
    }

    // Static method to provide access to the single instance
    public static SocketErrorMessage getInstance() {
        if (instance == null) {
            synchronized (SocketErrorMessageSingleton.class) {
                if (instance == null) {
                    instance = new SocketErrorMessage();
                }
            }
        }
        return instance;
    }
}

