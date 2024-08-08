package com.turingSecApp.turingSec.filter.websocket;

import com.turingSecApp.turingSec.config.websocket.headerAccessorAdapter.StompHeaderAccessorAdapter;
import com.turingSecApp.turingSec.model.repository.CustomCsrfTokenRepository;
import com.turingSecApp.turingSec.exception.websocket.exceptionHandling.SocketExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class CsrfChannelInterceptor implements ChannelInterceptor {

    //todo: generate actual csrf in db for every user not static
    private final CustomCsrfTokenRepository csrfTokenRepository;
    private final SocketExceptionHandler socketExceptionHandler;

    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // Wrap the message to access its headers
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompHeaderAccessorAdapter accessorAdapter = new StompHeaderAccessorAdapter(accessor);

        Message<?> messageFromHandler = socketExceptionHandler.executeWithExceptionHandling(() -> {

            // When command is CONNECT this checks csrf
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                checkCSRF(accessor);
            }
        }, accessorAdapter, message);

        // Return the original message if validation passes, if error occurs it sets message as exception message
        return messageFromHandler;
    }


    private void checkCSRF(StompHeaderAccessor accessor) {
            // Retrieve the CSRF token from the message headers
            String actualTokenValue = accessor.getFirstNativeHeader("X-CSRF-TOKEN");


            if (actualTokenValue == null) {
                log.error("CSRF token is missing in CONNECT headers");
                throw new IllegalArgumentException("CSRF Token is missing");
            }

            // Load the expected CSRF token using the current request
            CsrfToken expectedToken = csrfTokenRepository.loadToken();
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
}
