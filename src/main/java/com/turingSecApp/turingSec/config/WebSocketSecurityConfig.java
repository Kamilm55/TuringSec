//package com.turingSecApp.turingSec.config;
//
//import com.turingSecApp.turingSec.response.message.BaseMessageInReportDTO;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authorization.AuthorizationManager;
//
//@Configuration
//public class WebSocketSecurityConfig {
//
//    @Bean
//    public AuthorizationManager<BaseMessageInReportDTO> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
//        messages
//                .nullDestMatcher().authenticated()
//                .simpSubscribeDestMatchers("/user/queue/errors").permitAll()
//                .simpDestMatchers("/app/**").hasRole("USER")
//                .simpSubscribeDestMatchers("/user/**", "/topic/friends/*").hasRole("USER")
//                .anyMessage().denyAll();
//
//        return messages.build();
//    }
//}