package com.turingSecApp.turingSec.response.base;

import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseException extends RuntimeException{
    ResponseMessages responseMessages;

    @Override
    public String getMessage() {
        return responseMessages.getMessage();
    }

//    public static BaseException unexpected(){
//        return BaseException.builder()
//                .responseMessages(UNEXPECTED)
//                .build();
//    }

    // Custom exception (generic type)
    public static BaseException of(ResponseMessages responseMessages){
        return BaseException.builder()
                .responseMessages(responseMessages)
                .build();
    }

}

