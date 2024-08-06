package com.turingSecApp.turingSec.config.websocket.adapter;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

public class StompHeaderAccessorAdapter implements CustomHeaderAccessor {
    private final StompHeaderAccessor accessor;

    public StompHeaderAccessorAdapter(StompHeaderAccessor accessor) {
        this.accessor = accessor;
    }

    @Override
    public Object getHeader(String headerName) {
        return accessor.getHeader(headerName);
    }

    @Override
    public void setDestination(String destination) {
        accessor.setDestination(destination);
    }

    @Override
    public String getSessionId() {
        return accessor.getSessionId();
    }
}

