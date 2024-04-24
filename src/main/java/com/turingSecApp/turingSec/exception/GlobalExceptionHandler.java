package com.turingSecApp.turingSec.exception;

import com.turingSecApp.turingSec.exception.custom.*;
import com.turingSecApp.turingSec.response.base.ExceptionResponseMessages;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;

@ControllerAdvice
public class GlobalExceptionHandler {
    // User Controller
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponseMessages> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {

        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.CONFLICT) ,
                HttpStatus.CONFLICT
        );

    }
    //org.springframework.mail.MailSendException
    @ExceptionHandler(org.springframework.mail.MailSendException.class)
    public ResponseEntity<ExceptionResponseMessages> handleMailSendException(org.springframework.mail.MailSendException ex) {

        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.BAD_REQUEST) ,
                HttpStatus.BAD_REQUEST
        );

    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ExceptionResponseMessages> handleInvalidTokenException(InvalidTokenException ex) {
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.BAD_REQUEST) ,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponseMessages> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.BAD_REQUEST) ,
                HttpStatus.BAD_REQUEST
        );
    }
    //TODO


    @ExceptionHandler(com.turingSecApp.turingSec.exception.custom.BadCredentialsException.class)
    public ResponseEntity<ExceptionResponseMessages> handleBadCredentials(com.turingSecApp.turingSec.exception.custom.BadCredentialsException ex) {
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.CONFLICT) ,
                HttpStatus.CONFLICT
        );
    }


    @ExceptionHandler(UserNotActivatedException.class)
    public ResponseEntity<ExceptionResponseMessages> handleUserNotActivatedException(UserNotActivatedException ex) {
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.CONFLICT) ,
                HttpStatus.CONFLICT
        );
    }


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponseMessages> handleUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.NOT_FOUND) ,
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ExceptionResponseMessages> handleUnauthorizedException(UnauthorizedException ex) {
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.UNAUTHORIZED) ,
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponseMessages> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.CONFLICT) ,
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ExceptionResponseMessages> handleFileNotFndException(FileNotFoundException ex) {
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.NOT_FOUND) ,
                HttpStatus.NOT_FOUND
        );
    }
    // BackgroundImageForHackerController
    @ExceptionHandler(com.turingSecApp.turingSec.background_file_upload_for_hacker.exception.FileNotFoundException.class)
    public ResponseEntity<ExceptionResponseMessages> handleFileNotFoundException(com.turingSecApp.turingSec.background_file_upload_for_hacker.exception.FileNotFoundException ex) {
         return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.NOT_FOUND) ,
                HttpStatus.NOT_FOUND
        );
    }

    // ImageForHackerController for Image and video download
    @ExceptionHandler(com.turingSecApp.turingSec.file_upload_for_hacker.exception.FileNotFoundException.class)
    public ResponseEntity<ExceptionResponseMessages> handleFileNotFoundExceptionForImage(com.turingSecApp.turingSec.file_upload_for_hacker.exception.FileNotFoundException ex) {
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.NOT_FOUND) ,
                HttpStatus.NOT_FOUND
        );
    }
    // Company Controller
    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<ExceptionResponseMessages> handleCompanyNotFoundException(CompanyNotFoundException ex) {
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.NOT_FOUND) ,
                HttpStatus.NOT_FOUND
        );
    }

    // For unhandled exceptions:
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ExceptionResponseMessages> generalExceptionHandler(Exception ex){
        System.out.println("For unhandled exceptions");
        System.out.println(ex.getClass());
        return new ResponseEntity<>(
                new ExceptionResponseMessages(ex.getClass().getName(), ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR) ,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
