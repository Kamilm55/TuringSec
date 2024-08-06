package com.turingSecApp.turingSec.config.websocket;

import com.turingSecApp.turingSec.service.socket.exceptionHandling.SocketErrorMessage;
import com.turingSecApp.turingSec.service.socket.exceptionHandling.SocketExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketSessionDecorator;

import java.io.IOException;

@Slf4j
public class CustomWebSocketSessionDecorator extends WebSocketSessionDecorator {
    private final WebSocketSession session;

    public CustomWebSocketSessionDecorator(WebSocketSession session) {
        super(session);
        this.session = session;
    }

    @Override
    public void close() throws IOException {
        // Custom behavior before closing the session
        log.info("Preparing to close WebSocket session...");

        sendMessageWithSession();

        // Call the actual close method of the delegate
        super.close();
    }



    @Override
    public void close(CloseStatus status) throws IOException {
        // Custom behavior before closing the session
        log.info("Preparing to close WebSocket session with status: " + status);

        sendMessageWithSession();

        // Call the actual close method of the delegate with status
        super.close(status);
    }
    private void sendMessageWithSession() throws IOException {
        SocketErrorMessage socketErrorMessage = SocketErrorMessageSingleton.getInstance();

        System.out.println("socketErrorMessage values: " + socketErrorMessage);

        // Format the message as needed
        String formattedMessage = String.format("{\"destination\": \"%s\", \"payload\": \"%s\"}", "/topic/error", socketErrorMessage.toStringForWebsocketMsg());

        // Create the WebSocket message
        TextMessage message = new TextMessage(formattedMessage);

        // Send the message
        session.sendMessage(message);
    }
}

