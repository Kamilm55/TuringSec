package com.turingSecApp.turingSec.filter.websocket;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CsrfChannelInterceptor implements ChannelInterceptor {

    //todo: generate actual csrf in db for every user not static
    private final CsrfTokenRepository csrfTokenRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // Wrap the message to access its headers
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // Check if the message is a CONNECT command
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Retrieve the CSRF token from the message headers
            String actualTokenValue = accessor.getFirstNativeHeader("X-CSRF-TOKEN");

            log.info("Received CSRF token value: " + actualTokenValue);

            if (actualTokenValue == null) {
                log.error("CSRF token is missing in CONNECT headers");
                throw new IllegalArgumentException("CSRF Token is missing");
            }

            // Load the expected CSRF token using the current request
            CsrfToken expectedToken = /*csrfTokenRepository.loadToken(request);*/new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "123456789");
            if (expectedToken == null) {
                log.error("Expected CSRF token could not be found in the repository");
                throw new IllegalStateException("CSRF Token could not be found");
            }

            log.info("Expected CSRF token value: " + expectedToken.getToken());

            boolean csrfCheckPassed = expectedToken.getToken().equals(actualTokenValue);
            if (!csrfCheckPassed) {
                throw new InvalidCsrfTokenException(expectedToken, actualTokenValue);
            }

        }

        // Return the message if validation passes
        return message;
    }
}
