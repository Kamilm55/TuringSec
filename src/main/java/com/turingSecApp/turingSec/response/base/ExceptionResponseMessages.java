package com.turingSecApp.turingSec.response.base;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class ExceptionResponseMessages implements ResponseMessages  {

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
