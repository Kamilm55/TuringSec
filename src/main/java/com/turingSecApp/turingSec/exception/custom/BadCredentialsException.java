package com.turingSecApp.turingSec.exception.custom;

public class BadCredentialsException extends RuntimeException{
    private final String message;
    public BadCredentialsException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
