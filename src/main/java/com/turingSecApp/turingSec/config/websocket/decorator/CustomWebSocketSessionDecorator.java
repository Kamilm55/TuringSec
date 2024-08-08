package com.turingSecApp.turingSec.config.websocket.decorator;

import com.turingSecApp.turingSec.exception.websocket.exceptionHandling.SocketErrorMessage;
import com.turingSecApp.turingSec.exception.websocket.exceptionHandling.SocketErrorMessageSingleton;
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

        // Send error msf before close
        sendMessageWithSession();

        // Call the actual close method of the delegate
        super.close();
    }



    @Override
    public void close(CloseStatus status) throws IOException {
        // Custom behavior before closing the session
        log.info("Preparing to close WebSocket session with status: " + status);

        // Send error msf before close
        sendMessageWithSession();

        // Call the actual close method of the delegate with status
        super.close(status);
    }
    private void sendMessageWithSession() throws IOException {
        SocketErrorMessage socketErrorMessage = SocketErrorMessageSingleton.getInstance();

        // Constructing the error message manually in the stomp error format
        String errorMessage = String.format(
                "ERROR\nmessage:%s\ncontent-length:0\n\n\u0000",// ERROR message format for stomp (if the format is different client does not make sense)
                socketErrorMessage.toStringForWebsocketMsg()
        );

        // Create the WebSocket message
        TextMessage message = new TextMessage(errorMessage);

        // Send the message
        session.sendMessage(message); // If sendMessage is used with a specific WebSocket session, it will send the message only to that particular session. It sends to user which session id = current session id
    }
}

