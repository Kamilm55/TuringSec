package com.turingSecApp.turingSec.exception.custom;

public class UserMustBeSameWithReportUserException extends RuntimeException{
    private final String message;
    public UserMustBeSameWithReportUserException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
