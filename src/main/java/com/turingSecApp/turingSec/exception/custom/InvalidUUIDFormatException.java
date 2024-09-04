package com.turingSecApp.turingSec.exception.custom;

public class InvalidUUIDFormatException extends RuntimeException {
    public InvalidUUIDFormatException(String string, IllegalArgumentException e) {
        super(string,e);
    }
}

