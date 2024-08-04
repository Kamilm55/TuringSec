package com.turingSecApp.turingSec.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
public class WebSocketSecurityConfig   {

    //Note: it is not like http security filter chain

    // This method creates a bean of type AuthorizationManager<Message<?>>.
    // The AuthorizationManager is responsible for handling message-level authorization.
    // It takes a MessageMatcherDelegatingAuthorizationManager.Builder as a parameter.
    @Bean
    AuthorizationManager<Message<?>> messageAuthorizationManager() {

        MessageMatcherDelegatingAuthorizationManager.Builder messages = MessageMatcherDelegatingAuthorizationManager.builder();

        // The following code configures message-level authorization rules.
        messages
                .simpDestMatchers("/ws/**").authenticated()
                .anyMessage().denyAll();

        return messages.build();
    }

}