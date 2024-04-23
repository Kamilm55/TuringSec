package com.turingSecApp.turingSec.response.base;


import com.turingSecApp.turingSec.response.base.ResponseMessages;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public enum SuccessResponseMessages implements ResponseMessages {

    SUCCESS("success","Successful operation" ,HttpStatus.OK);

    String key;
    String message;
    HttpStatus status;

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}

