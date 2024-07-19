package com.turingSecApp.turingSec.config;

import com.corundumstudio.socketio.protocol.JacksonJsonSupport;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomJacksonJsonSupport extends JacksonJsonSupport {
    private ObjectMapper objectMapper;
    public CustomJacksonJsonSupport(ObjectMapper objectMapper) {
        super();
        this.objectMapper = objectMapper; // Use the provided ObjectMapper
        init(objectMapper);
    }
}
