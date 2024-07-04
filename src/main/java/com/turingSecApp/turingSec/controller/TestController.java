package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.response.base.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;


@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class TestController {

    @GetMapping
    public ResponseEntity<BaseResponse<String>> test() throws URISyntaxException {
        String data = "TEST WORKS SUCCESSFULLY!";
        URI uri = new URI("app/users"); //mock uri
        return BaseResponse.created(data,uri,"msg");
    }
}
