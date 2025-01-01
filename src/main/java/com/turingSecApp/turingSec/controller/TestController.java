package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.model.enums.EmailTemplate;
import com.turingSecApp.turingSec.model.repository.report.ReportRepository;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.service.EmailService;
import com.turingSecApp.turingSec.service.MockDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
public class TestController {
    private final ReportRepository reportRepository;
    private final MockDataService mockDataService;
    private final EmailService emailService;

    @GetMapping
    public ResponseEntity<BaseResponse<String>> test() throws URISyntaxException {
        String data = "TEST WORKS SUCCESSFULLY!";
        URI uri = new URI("app/users"); //mock uri
        return BaseResponse.created(data,uri,"msg");
    }
}
