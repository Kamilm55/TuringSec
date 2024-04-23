package com.turingSecApp.turingSec.exception.custom;

public class UnauthorizedException extends RuntimeException {

    private final String message;

    public UnauthorizedException(String message) {
        super(message);
        this.message = message;
    }

    public UnauthorizedException() {
        this("User is not authorized"); // Call the other constructor with default message
    }

    @Override
    public String getMessage() {
        return message;
    }
}
