package com.turingSecApp.turingSec.filter.websocket.custom;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

public interface IReportRoomInterceptor {
    void handleSend(String destination, StompHeaderAccessor accessor, Object authenticatedUser);
    void handleSubscription(String destination, StompHeaderAccessor accessor, Object authenticatedUser);
}
