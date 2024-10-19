package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.service.interfaces.INotificationSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SSEController {

    private final INotificationSseService sseService;

    @GetMapping("/notifications")
    public SseEmitter sendSse() {
        return sseService.addEmitter();
    }
}