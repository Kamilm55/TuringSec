//package com.turingSecApp.turingSec.config;
//
//import com.corundumstudio.socketio.SocketIOServer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class SocketConfig {
//
//    @Value("${server.port}")
//    private int socketPort;
//
//    @Value("${server.host}")
//    private String host;
//
//
//    @Bean
//    public SocketIOServer socketIOServer(){
//        com.corundumstudio.socketio.Configuration socketConfig = new com.corundumstudio.socketio.Configuration();
//        socketConfig.setHostname(host);
//        socketConfig.setPort(6000);// todo: for prod configure other port for socket
//
//        return new SocketIOServer(socketConfig);
//    }
//}
