package com.turingSecApp.turingSec.config.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turingSecApp.turingSec.filter.websocket.CsrfChannelInterceptor;
import com.turingSecApp.turingSec.filter.websocket.JwtChannelInterceptor;
import com.turingSecApp.turingSec.filter.websocket.ReportRoomInterceptor;
import com.turingSecApp.turingSec.service.socket.exceptionHandling.SocketErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtChannelInterceptor jwtChannelInterceptor;
    private final ReportRoomInterceptor reportRoomInterceptor;
    private final CsrfChannelInterceptor csrfChannelInterceptor;


    @Bean
    public SocketErrorMessage socketErrorMessage() {
        return new SocketErrorMessage();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable an in-memory message broker with destinations prefixed with /topic and /queue
        registry.enableSimpleBroker("/topic", "/queue");

        // Sets the prefix for application destinations. Messages sent from clients with destinations
        // starting with "/app" will be routed to methods annotated with @MessageMapping.
        registry.setApplicationDestinationPrefixes("/app");

        // Sets the prefix used for destinations targeting specific users. This is typically used for
        // point-to-point messaging, where a message is sent to a specific user.
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Adds a WebSocket endpoint at "/ws".
        // Clients will connect to this endpoint to establish a WebSocket connection.
        registry.addEndpoint("/ws")
                // Allows connections from any origin to avoid CORS issues.
                // For a more secure setup, specify the allowed origins explicitly.
                .setAllowedOriginPatterns("*")
                // Enables SockJS fallback options if WebSocket is not available.
                .withSockJS();
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        // Create a content type resolver to handle default MIME type.
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();

        // Set the default MIME type to application/json.
        // Todo: Consider changing to application/octet-stream for handling files and JSON simultaneously.
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);

        // Create a message converter that uses Jackson for JSON conversion.
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();

        // Set a custom ObjectMapper for JSON processing.
        converter.setObjectMapper(new ObjectMapper());

        // Set the content type resolver to the converter.
        converter.setContentTypeResolver(resolver);

        // Add the custom converter to the list of message converters.
        messageConverters.add(converter);

        // Return false to indicate not to use default message converters from the registry.
        return false;
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // 1.csrfChannelInterceptor -> (todo) 2.authChannelInterceptor -> 3. ReportRoomInterceptor
        registration.interceptors(csrfChannelInterceptor,reportRoomInterceptor);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {

        registration.addDecoratorFactory(handler -> new CustomWebSocketHandlerDecorator(handler));


    }


}
