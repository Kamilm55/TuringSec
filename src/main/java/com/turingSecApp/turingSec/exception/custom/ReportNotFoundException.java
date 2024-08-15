package com.turingSecApp.turingSec.exception.custom;

public class ReportNotFoundException extends RuntimeException {
    private final String message;

    public ReportNotFoundException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
