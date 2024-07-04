package com.turingSecApp.turingSec.response.base;


import com.turingSecApp.turingSec.response.base.ResponseMessages;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public enum SuccessResponseMessages implements ResponseMessages {

    SUCCESS("SUCCESS","Successful operation" ,HttpStatus.OK),
    CREATED("CREATED","Product created successfully" , HttpStatus.CREATED),
    NO_CONTENT("NO_CONTENT", "", HttpStatus.NO_CONTENT);

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

