package com.turingSecApp.turingSec.exception;

import com.turingSecApp.turingSec.exception.custom.*;
import com.turingSecApp.turingSec.response.base.ExceptionResponseMessages;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final Environment environment;

    // Helper method to print stack trace if 'dev' profile is active
    private void printStackTraceIfDevProfileActive(Exception ex) {
        if (Arrays.asList(environment.getActiveProfiles()).contains("dev")) {
            ex.printStackTrace();
        }
    }
    // User Controller
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponseMessages> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        printStackTraceIfDevProfileActive(ex);
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.CONFLICT) ,
                HttpStatus.CONFLICT
        );

    }

    @ExceptionHandler(UserMustBeSameWithReportUserException.class)
    public ResponseEntity<ExceptionResponseMessages> handleUserMustBeSameWithReportUserException(UserMustBeSameWithReportUserException ex) {
        printStackTraceIfDevProfileActive(ex);
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.CONFLICT) ,
                HttpStatus.CONFLICT
        );

    }
    //org.springframework.mail.MailSendException
    @ExceptionHandler(org.springframework.mail.MailSendException.class)
    public ResponseEntity<ExceptionResponseMessages> handleMailSendException(org.springframework.mail.MailSendException ex) {
        printStackTraceIfDevProfileActive(ex);
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.BAD_REQUEST) ,
                HttpStatus.BAD_REQUEST
        );

    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ExceptionResponseMessages> handleInvalidTokenException(InvalidTokenException ex) {
        printStackTraceIfDevProfileActive(ex);
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.BAD_REQUEST) ,
                HttpStatus.BAD_REQUEST
        );
    }
    @ExceptionHandler(InvalidUUIDFormatException.class)
    public ResponseEntity<ExceptionResponseMessages> handleInvalidUUIDFormatException(InvalidUUIDFormatException ex) {
        printStackTraceIfDevProfileActive(ex);

        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.BAD_REQUEST) ,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponseMessages> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        printStackTraceIfDevProfileActive(ex);
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.BAD_REQUEST) ,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(PermissionDeniedException.class)
    public ResponseEntity<ExceptionResponseMessages> handleEPermissionDeniedException(PermissionDeniedException ex) {
        printStackTraceIfDevProfileActive(ex);
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.FORBIDDEN) ,
                HttpStatus.FORBIDDEN
        );
    }

    //  this is not from security layer of spring, it ios custom bad credentials
    @ExceptionHandler(com.turingSecApp.turingSec.exception.custom.BadCredentialsException.class)
    public ResponseEntity<ExceptionResponseMessages> handleBadCredentials(com.turingSecApp.turingSec.exception.custom.BadCredentialsException ex) {
        printStackTraceIfDevProfileActive(ex);
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.UNAUTHORIZED) ,
                HttpStatus.UNAUTHORIZED
        );
    }
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ExceptionResponseMessages> handleBadCredentials(org.springframework.security.authentication.BadCredentialsException ex) {
        printStackTraceIfDevProfileActive(ex);
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.UNAUTHORIZED) ,
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(UserNotActivatedException.class)
    public ResponseEntity<ExceptionResponseMessages> handleUserNotActivatedException(UserNotActivatedException ex) {
        printStackTraceIfDevProfileActive(ex);
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.CONFLICT) ,
                HttpStatus.CONFLICT
        );
    }
    @ExceptionHandler(CollaboratorException.class)
    public ResponseEntity<ExceptionResponseMessages> handleCollaboratorException(CollaboratorException ex) {
        printStackTraceIfDevProfileActive(ex);
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.CONFLICT) ,
                HttpStatus.CONFLICT
        );
    }


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponseMessages> handleUserNotFoundException(UserNotFoundException ex) {
        printStackTraceIfDevProfileActive(ex);
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.NOT_FOUND) ,
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ExceptionResponseMessages> handleUnauthorizedException(UnauthorizedException ex) {
        printStackTraceIfDevProfileActive(ex);
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.UNAUTHORIZED) ,
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponseMessages> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.BAD_REQUEST) ,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<ExceptionResponseMessages> handleReportNotFoundException(ReportNotFoundException ex) {
        printStackTraceIfDevProfileActive(ex);
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.NOT_FOUND) ,
                HttpStatus.NOT_FOUND
        );
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponseMessages> handleResourceNotFoundException(ResourceNotFoundException ex) {
        printStackTraceIfDevProfileActive(ex);
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.NOT_FOUND) ,
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ExceptionResponseMessages> handleFileNotFndException(FileNotFoundException ex) {
        printStackTraceIfDevProfileActive(ex);
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.NOT_FOUND) ,
                HttpStatus.NOT_FOUND
        );
    }
    // BackgroundImageForHackerController
    @ExceptionHandler(com.turingSecApp.turingSec.file_upload.exception.FileNotFoundException.class)
    public ResponseEntity<ExceptionResponseMessages> handleFileNotFoundException(com.turingSecApp.turingSec.file_upload.exception.FileNotFoundException ex) {
        printStackTraceIfDevProfileActive(ex);
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.NOT_FOUND) ,
                HttpStatus.NOT_FOUND
        );
    }

    // Company Controller
    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<ExceptionResponseMessages> handleCompanyNotFoundException(CompanyNotFoundException ex) {
        printStackTraceIfDevProfileActive(ex);
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.NOT_FOUND) ,
                HttpStatus.NOT_FOUND
        );
    }

    // Validation exceptions
    // Learn:
    //  MethodArgumentNotValidException is specific to Spring and is thrown when there are validation errors during the binding of method parameters, typically in a Spring MVC controller method.
    //  It is commonly used when validating incoming request data, such as form submissions or JSON payloads,
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        printStackTraceIfDevProfileActive(ex);
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    // Learn:
    //  It is typically thrown when there are constraint violations during the validation of entities or objects, usually when using annotations like @NotNull, @Size, @Email, etc., on fields or properties of a Java class.
    //  This exception can occur when validating entities outside the context of Spring MVC, for example, in a JPA (Java Persistence API) environment.
    //  If you try to persist a User object with a null username using JPA, a ConstraintViolationException may be thrown
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex) {
        printStackTraceIfDevProfileActive(ex);
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        }
        return new ResponseEntity<>(errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    // For unhandled exceptions:
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ExceptionResponseMessages> generalExceptionHandler(Exception ex){
        printStackTraceIfDevProfileActive(ex);
        System.out.println("For unhandled exceptions");
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR) ,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
