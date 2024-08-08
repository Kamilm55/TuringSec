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
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationChannelInterceptor implements ChannelInterceptor {
    private final JwtUtil jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final CustomWebsocketSecurityContext customWebsocketSecurityContext;

    private final SocketExceptionHandler socketExceptionHandler;

    @SneakyThrows // It must be handled by native websocket exception handler
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
     // Wrap the message to access its headers
     StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
     StompHeaderAccessorAdapter accessorAdapter = new StompHeaderAccessorAdapter(accessor);

        Message<?> messageFromHandler = socketExceptionHandler.executeWithExceptionHandling(() -> {

            // Check if the message is a CONNECT,SUBSCRIBE,SEND command -> it must be authorized to achieve this
            if (StompCommand.CONNECT.equals(accessor.getCommand()) | StompCommand.SUBSCRIBE.equals(accessor.getCommand()) | StompCommand.SEND.equals(accessor.getCommand())) {
                log.info("Message" + message);
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

        }, accessorAdapter, message);

        // Return the original message if validation passes, if error occurs it sets message as exception message
        return messageFromHandler;
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
        accessor.setUser(auth);
//        SecurityContextHolder.getContext().setAuthentication(auth); // It does not work for websocket
        customWebsocketSecurityContext.setAuthentication(auth); // Customize SecurityContextHolder.getContext() for websocket

        return true;
    }
}
