package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.model.entities.user.UserEntityI;
import com.turingSecApp.turingSec.model.repository.report.ReportsRepository;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.service.MockDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
@Slf4j
public class TestController {
    private final ReportsRepository reportsRepository;
    private final MockDataService mockDataService;

    @GetMapping
    public ResponseEntity<BaseResponse<String>> test() throws URISyntaxException {
        String data = "TEST WORKS SUCCESSFULLY!";
        URI uri = new URI("app/users"); //mock uri
        return BaseResponse.created(data,uri,"msg");
    }
    @GetMapping("/lazyInit")
    public BaseResponse<Void> testLazyInit() {
//        testProxy();

        mockDataService.testProxy();
        return BaseResponse.success();
    }

    @Transactional
    public void testProxy() {

        Report report = reportsRepository.findById(1L).orElse(null);

        log.info("report user: " + report.getUser());

        UserEntityI userOfReportMessage = report.getUser();
        System.out.println(userOfReportMessage);
    }
}
