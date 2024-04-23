package com.turingSecApp.turingSec.response.base;

import lombok.Data;
import org.springframework.http.HttpStatus;


public interface ResponseMessages {
    String getKey();
    String getMessage();
    HttpStatus getStatus();
}