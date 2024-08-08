package com.turingSecApp.turingSec.config.websocket.headerAccessorAdapter;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public class SimpHeaderAccessorAdapter implements CustomHeaderAccessor {
    private final SimpMessageHeaderAccessor accessor;

    public SimpHeaderAccessorAdapter(SimpMessageHeaderAccessor accessor) {
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

