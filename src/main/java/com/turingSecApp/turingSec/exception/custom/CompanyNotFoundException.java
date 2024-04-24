package com.turingSecApp.turingSec.exception.custom;

public class CompanyNotFoundException extends RuntimeException {
    private final String message;

    public CompanyNotFoundException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
