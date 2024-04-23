package com.turingSecApp.turingSec.exception.custom;


public class InvalidTokenException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Invalid activation token.";

    public InvalidTokenException() {
        super(DEFAULT_MESSAGE);
    }
}
