package com.turingSecApp.turingSec.filter.websocket;

import com.turingSecApp.turingSec.config.websocket.security.CustomWebsocketSecurityContext;
import com.turingSecApp.turingSec.config.websocket.headerAccessorAdapter.StompHeaderAccessorAdapter;
import com.turingSecApp.turingSec.exception.custom.UnauthorizedException;
import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.service.user.UserDetailsServiceImpl;
import com.turingSecApp.turingSec.exception.websocket.exceptionHandling.SocketExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationChannelInterceptor implements ChannelInterceptor {
    private final JwtUtil jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final CustomWebsocketSecurityContext customWebsocketSecurityContext;

    private final SocketExceptionHandler socketExceptionHandler;

    @SneakyThrows // It must be handled by native websocket exception handler
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // Wrap the message to access its headers
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompHeaderAccessorAdapter accessorAdapter = new StompHeaderAccessorAdapter(accessor);

        socketExceptionHandler.executeWithExceptionHandling(() -> {
            // Check if the message command requires authorization
            // CONNECT | SUBSCRIBE | SEND
            if (isAuthorizationRequired(accessor.getCommand())) {
                log.info("Message: {}", message);
                log.info("Message Headers: {}", message.getHeaders());
                log.info("Session id: {}", accessor.getSessionId());

                // Extract and validate the Authorization header
                validateAuthorizationHeader(message);

                // Set authentication based on the Authorization header
                String authorizationHeader = extractAuthorizationHeader(message);
                if (!setAuth(authorizationHeader, accessor)) {
                    throw new UnauthorizedException();
                }
            }

        }, accessorAdapter, message);

        // Return the original message if validation passes
        return message;
    }

    private boolean isAuthorizationRequired(StompCommand command) {
        return StompCommand.CONNECT.equals(command) ||
                StompCommand.SUBSCRIBE.equals(command) ||
                StompCommand.SEND.equals(command);
    }

    private boolean setAuth(String authorizationHeader, StompHeaderAccessor accessor) {
//        log.info("Authorization Header: {}", authorizationHeader);

        // Extract and validate the token from the header
        String token = jwtTokenProvider.resolveToken(authorizationHeader);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return false;
        }

        // Extract the username from the token
        String username = jwtTokenProvider.getUserIdAsUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // If user details cannot be loaded, authentication fails
        if (userDetails == null) {
            return false;
        }

        // Log user details for debugging purposes
        log.info("UserDetails of current user: {}", userDetails);

        // Set the authenticated user in the security context
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        accessor.setUser(auth);
//        SecurityContextHolder.getContext().setAuthentication(auth); // It does not work for websocket
        customWebsocketSecurityContext.setAuthentication(auth); // Customize SecurityContextHolder.getContext() for websocket

        return true;
    }


    // Utils
    private void validateAuthorizationHeader(Message<?> message) {
        Map<String, Object> nativeHeaders = (Map<String, Object>) message.getHeaders().get("nativeHeaders");
        if (nativeHeaders == null || nativeHeaders.get("Authorization") == null) {
            throw new UnauthorizedException("There is no Authorization header");
        }
    }

    private String extractAuthorizationHeader(Message<?> message) {
        Map<String, Object> nativeHeaders = (Map<String, Object>) message.getHeaders().get("nativeHeaders");
        List<String> authorizationHeaderList = (List<String>) nativeHeaders.get("Authorization");
        if (authorizationHeaderList == null || authorizationHeaderList.isEmpty() || authorizationHeaderList.get(0) == null) {
            throw new UnauthorizedException("Authorization header is empty or null");
        }
        return authorizationHeaderList.get(0);
    }
}
