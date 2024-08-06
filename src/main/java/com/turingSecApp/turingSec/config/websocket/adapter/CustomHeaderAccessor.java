package com.turingSecApp.turingSec.config.websocket.adapter;

import org.springframework.lang.Nullable;

public interface CustomHeaderAccessor {
    Object getHeader(String headerName);
    void setDestination(@Nullable String destination);
    String getSessionId();
}
