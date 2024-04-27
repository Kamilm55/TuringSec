package com.turingSecApp.turingSec.exception.custom;

public class PermissionDeniedException extends RuntimeException {

    public PermissionDeniedException(String message) {
        super(message);
    }

    public PermissionDeniedException() {
        this("User has no appropriate authority for this operation. FORBIDDEN"); // Call the other constructor with default message
    }
}
