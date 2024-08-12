package com.turingSecApp.turingSec.exception.custom;

public class UnauthorizedException extends RuntimeException {

    private static final String UNAUTHORIZED_MSG = "User is not authorized";
    private static final String CUSTOM_EX_MESSAGE = UNAUTHORIZED_MSG + " -> %s";
    private final String message;

    public UnauthorizedException(String customMessage) {
        super(String.format(CUSTOM_EX_MESSAGE, customMessage));
        this.message = String.format(CUSTOM_EX_MESSAGE, customMessage);
    }

    public UnauthorizedException() {
        super(UNAUTHORIZED_MSG);
        this.message = UNAUTHORIZED_MSG;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
