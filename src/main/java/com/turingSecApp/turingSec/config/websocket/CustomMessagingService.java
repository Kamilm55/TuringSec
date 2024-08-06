package com.turingSecApp.turingSec.config.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomMessagingService {
    // Prevent direct inject SimpMessagingTemplate to interceptor, it causes circular flow

    private final SimpMessagingTemplate messagingTemplate;

    public void sendMessage(String destination, Object payload) {
        messagingTemplate.convertAndSend(destination, payload);
    }
}
