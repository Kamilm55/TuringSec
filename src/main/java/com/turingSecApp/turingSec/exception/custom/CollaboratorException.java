package com.turingSecApp.turingSec.exception.custom;

public class CollaboratorException extends RuntimeException {
    private final String message;

    public CollaboratorException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
