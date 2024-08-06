package com.turingSecApp.turingSec.config.websocket;

import com.turingSecApp.turingSec.service.socket.exceptionHandling.SocketErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

@Component
@Slf4j
public class CustomWebSocketHandlerDecorator extends WebSocketHandlerDecorator {
    private static final Logger logger = LoggerFactory.getLogger(CustomWebSocketHandlerDecorator.class);


    public CustomWebSocketHandlerDecorator(@Qualifier("subProtocolWebSocketHandler")WebSocketHandler delegate) {
        super(delegate);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            WebSocketSession decoratedSession = new CustomWebSocketSessionDecorator(session);

            log.info("Custom WebSocketSession:" + decoratedSession);
            super.afterConnectionEstablished(decoratedSession);
        } catch (Exception ex) {
            handleException(session, ex);
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        try {
            super.handleMessage(session, message);
        } catch (Exception ex) {
        System.out.println("handleMessage");
            handleException(session, ex);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        try {
            super.handleTransportError(session, exception);
        } catch (Exception ex) {
        System.out.println("handleTransportError");
            handleException(session, ex);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        try {
            super.afterConnectionClosed(session, closeStatus);
        } catch (Exception ex) {
        System.out.println("afterConnectionClosed");
            handleException(session, ex);
        }
    }

    private void handleException(WebSocketSession session, Exception ex) {
        try {

            System.out.println("///");
            logger.error("WebSocket error in session " + session.getId(), ex);
            System.out.println("///");


//            stompController.sendErrorMessage(session.getId(), "Error: " + ex.getMessage());
        } catch (Exception sendEx) {
            logger.error("Failed to send error message to session " + session.getId(), sendEx);
        }
    }
}

