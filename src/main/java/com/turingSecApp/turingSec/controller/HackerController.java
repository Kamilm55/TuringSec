package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.response.HackerDTO;
import com.turingSecApp.turingSec.response.HackerResponse;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.service.HackerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api/hacker")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class HackerController {
    private final HackerService hackerService;

    public HackerController(HackerService hackerService) {
        this.hackerService = hackerService;
    }

    @GetMapping("/{id}")
    public BaseResponse<HackerDTO> getById(@PathVariable Long id) throws FileNotFoundException {
        return BaseResponse.success(hackerService.findById(id));
    }
}
