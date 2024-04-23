package com.turingSecApp.turingSec.response.base;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)// when an object of this class is serialized to JSON, any properties that have null values will not be included in the resulting JSON output.
@Slf4j
public class BaseResponse<T>{

    HttpStatus status;
    Meta meta;
    T data;

    @Data
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Meta {
        String key;
        String message;

        // this is just builder
        private static Meta of(String key , String message){
            return Meta.builder()
                    .key(key)
                    .message(message)
                    .build();
        }

        private static Meta of(ResponseMessages responseMessages){
            return Meta.builder()
                    .key(responseMessages.getKey())
                    .message(responseMessages.getMessage())
                    .build();
        }

        private static Meta of(BaseException exception){
            String key = exception.getResponseMessages().getKey();
            String message = exception.getResponseMessages().getMessage();


            return of(
                    exception.getResponseMessages().getKey(),
                    exception.getResponseMessages().getMessage()
            );

        }
    }


    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>().<T>builder()
                .status(HttpStatus.OK)
                .meta(Meta.of(SuccessResponseMessages.SUCCESS))
                .data(data)
                .build();
    }
    public static <T> BaseResponse<T> success(T data,String additionalMsg){
        return new BaseResponse<>().<T>builder()
                .status(HttpStatus.OK)
                .meta(Meta.of(
                        "success",
                        additionalMsg
                ))
                .data(data)
                .build();
    }

    //overload without data
    public static <T> BaseResponse<T> success(){
        return  new BaseResponse<>().<T>builder()
                .status(HttpStatus.OK)
                .meta(Meta.of(SuccessResponseMessages.SUCCESS))
                .data(null)
                .build();
    }



//    public static <T> BaseResponse<T> error(BaseException baseException){
//
//        return new BaseResponse<>().<T>builder()
//                .status(baseException.getResponseMessages().getStatus())
//                .meta(
//                        Meta.of(baseException)
//                )
//                .data(null)
//                .build();
//    }

//    public static <T> BaseResponse<T> error(BaseException baseException, T data){
//        ResponseMessages resMessages = baseException.getResponseMessages();
//
//        return new BaseResponse<>().<T>builder()
//                .status(resMessages.getStatus())
//                .meta(Meta.of(resMessages.getKey(), resMessages.getMessage()))
//                .data(data)
//                .build();
//    }

}

