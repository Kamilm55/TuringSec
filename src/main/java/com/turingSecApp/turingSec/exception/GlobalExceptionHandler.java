package com.turingSecApp.turingSec.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailAlreadyExistsException(EmailAlreadyExistsException em) {
        return new ResponseEntity<>(em.getMessage(), HttpStatus.CONFLICT);
    }
    //TODO


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(BadCredentialsException em) {
        return new ResponseEntity<>(em.getMessage(), HttpStatus.CONFLICT);
    }


    @ExceptionHandler(UserNotActivatedException.class)
    public ResponseEntity<String> handleUserNotActivatedException(UserNotActivatedException em) {
        return new ResponseEntity<>(em.getMessage(), HttpStatus.CONFLICT);
    }


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException em) {
        return new ResponseEntity<>(em.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException em) {
        return new ResponseEntity<>(em.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException em) {
        return new ResponseEntity<>(em.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<String> handleFileNotFoundException(FileNotFoundException em) {
        return new ResponseEntity<>(em.getMessage(), HttpStatus.NOT_FOUND);
    }
    // BackgroundImageForHackerController
    @ExceptionHandler(com.turingSecApp.turingSec.background_file_upload_for_hacker.exception.FileNotFoundException.class)
    public ResponseEntity<String> handleFileNotFoundException(com.turingSecApp.turingSec.background_file_upload_for_hacker.exception.FileNotFoundException em) {
        return new ResponseEntity<>(em.getMessage(), HttpStatus.NOT_FOUND);
    }

    // ImageForHackerController for Image download
    @ExceptionHandler(com.turingSecApp.turingSec.file_upload_for_hacker.exception.FileNotFoundException.class)
    public ResponseEntity<String> handleFileNotFoundExceptionForImage(com.turingSecApp.turingSec.file_upload_for_hacker.exception.FileNotFoundException em) {
        return new ResponseEntity<>(em.getMessage(), HttpStatus.NOT_FOUND);
    }

    // For unhandled exceptions:
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> generalExceptionHandler(Exception exception){
        System.out.println("For unhandled exceptions");
        System.out.println(exception.getClass());
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
