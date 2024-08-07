package com.turingSecApp.turingSec.filter.websocket;

import com.turingSecApp.turingSec.config.websocket.CustomWebsocketSecurityContext;
import com.turingSecApp.turingSec.config.websocket.adapter.StompHeaderAccessorAdapter;
import com.turingSecApp.turingSec.exception.custom.UnauthorizedException;
import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.model.repository.CustomCsrfTokenRepository;
import com.turingSecApp.turingSec.service.socket.exceptionHandling.SocketExceptionHandler;
import com.turingSecApp.turingSec.service.user.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
@Slf4j
//todo: seperate csrf and jwt channel
public class CsrfChannelInterceptor implements ChannelInterceptor {

    //todo: generate actual csrf in db for every user not static
    private final CustomCsrfTokenRepository csrfTokenRepository;
    private final JwtUtil jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final CustomWebsocketSecurityContext customWebsocketSecurityContext;

    private final SocketExceptionHandler socketExceptionHandler;

    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // Wrap the message to access its headers
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompHeaderAccessorAdapter accessorAdapter = new StompHeaderAccessorAdapter(accessor);

        Message<?> errMessage = socketExceptionHandler.executeWithExceptionHandling(() -> {
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                // Check csrf
                checkCSRF(accessor);
            }

            // Check if the message is a CONNECT command , it can be a SUBSCRIBE command or other
            if (StompCommand.CONNECT.equals(accessor.getCommand()) | StompCommand.SUBSCRIBE.equals(accessor.getCommand()) | StompCommand.SEND.equals(accessor.getCommand())) {
                log.info("Message Headers: " + message.getHeaders());
                log.info("Session id:" + accessor.getSessionId());

                Map<String, Object> nativeHeaders = (Map<String, Object>) message.getHeaders().get("nativeHeaders");

                // Get the Authorization header value, it is in List format -> In STOMP (Streaming Text Oriented Messaging Protocol) over WebSockets, headers can sometimes be represented as List<String> because STOMP allows for multiple values for a single header key. This is different from typical HTTP headers, where each header key usually has a single value.
                List<String> authorizationHeaderList = (List<String>) nativeHeaders.get("Authorization");

                String authorizationHeader = authorizationHeaderList.get(0);

                // Set Auth if authorizationHeader is not null, if it is null throw unauthorized exception
                if (!setAuth(authorizationHeader, accessor)) {
                    throw new UnauthorizedException();
                }
            }
//            accessor.setHeader("sessionId", accessor.getSessionId());

        }, accessorAdapter, message);
//
//
        if (errMessage != null) {
            return errMessage;
        }
        // Return the message if validation passes
        return message;
    }

    private boolean setAuth(String authorizationHeader, StompHeaderAccessor accessor) {
//        log.info("Authorization Header: {}", authorizationHeader);

        // If no authorization header is present, authentication fails
        if (authorizationHeader == null) {
            return false;
        }

        // Extract and validate the token from the header
        String token = jwtTokenProvider.resolveToken(authorizationHeader);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return false;
        }

        // Extract the username from the token
        String username = jwtTokenProvider.getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // If user details cannot be loaded, authentication fails
        if (userDetails == null) {
            return false;
        }

        // Log user details for debugging purposes
        log.info("UserDetails of current user: {}", userDetails);

        // Set the authenticated user in the security context
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        accessor.setUser(auth);


       customWebsocketSecurityContext.setAuthentication(auth);
        return true;
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
