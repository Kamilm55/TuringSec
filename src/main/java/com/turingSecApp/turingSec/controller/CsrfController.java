package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.model.repository.CustomCsrfTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/csrf")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class CsrfController {
    private final String exampleToken = "123456789";
    private final CustomCsrfTokenRepository customCsrfTokenRepository;

    @GetMapping("/csrf-token")
    public CsrfToken getCsrfToken(HttpServletRequest request) {
        // Create a mock CsrfToken with a static value
        return customCsrfTokenRepository.generateToken(request);
    }
}
