package com.turingSecApp.turingSec.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.protocol.JacksonJsonSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SocketConfig {

    private final ObjectMapper objectMapper;

    @Value("${server.port}")
    private int socketPort;

    @Value("${server.host}")
    private String host;


    @Bean
    public SocketIOServer socketIOServer(){
        com.corundumstudio.socketio.Configuration socketConfig = new com.corundumstudio.socketio.Configuration();
        socketConfig.setHostname(host);
        socketConfig.setPort(4000);// todo: for prod configure other port for socket


        // Set CORS configuration
        socketConfig.setOrigin("http://localhost:5173"); // Allow all origins for development; change for production

        // Inject custom JacksonJsonSupport to enable serialization of LocalDateTime
        socketConfig.setJsonSupport(new JacksonJsonSupport());


        return new SocketIOServer(socketConfig);
    }
}
