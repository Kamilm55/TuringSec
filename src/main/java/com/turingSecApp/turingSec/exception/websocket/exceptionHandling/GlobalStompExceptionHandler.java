package com.turingSecApp.turingSec.exception.websocket.exceptionHandling;

import com.turingSecApp.turingSec.response.base.ExceptionResponseMessages;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ControllerAdvice
@Slf4j
public class GlobalStompExceptionHandler {


    // Validation
    @MessageExceptionHandler(value = org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException.class)
    @SendToUser("/queue/errors")
    public Map<String, String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        errors.put("Http_status", String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY));

        log.error("Validation Error: " + errors);
        return errors;
    }

    @MessageExceptionHandler(value = ConstraintViolationException.class)
    @SendToUser("/queue/errors")
    public Map<String, String> handleConstraintViolationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        }
        return errors;
    }

    // Custom exceptions
    @MessageExceptionHandler(IllegalArgumentException.class)
    @SendToUser("/queue/errors")
    public ExceptionResponseMessages handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ExceptionResponseMessages(
                ex.getClass().getName(),
                ex.getMessage(),
                HttpStatus.CONFLICT
        );
    }


    // General
    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public ExceptionResponseMessages handleGenericException(Exception ex) {
        log.error("Unhandled exception:", ex);
        return new ExceptionResponseMessages(
                ex.getClass().getName(),
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
