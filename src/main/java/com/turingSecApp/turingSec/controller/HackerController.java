package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.response.HackerDTO;
import com.turingSecApp.turingSec.response.HackerResponse;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.service.HackerService;
import com.turingSecApp.turingSec.service.interfaces.IHackerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api/hacker")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class HackerController {
    private final IHackerService hackerService;

    @GetMapping("/{id}")
    public BaseResponse<HackerDTO> getById(@PathVariable Long id) throws FileNotFoundException {
        return BaseResponse.success(hackerService.findById(id));
    }
}
