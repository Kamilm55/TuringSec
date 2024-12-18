package com.turingSecApp.turingSec.exception.websocket.exceptionHandling;

import lombok.Data;
import lombok.ToString;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

@Data
public class SocketErrorMessage implements Message {
    private String sessionId;
    private String key;
    private String message;
    private String stackTrace;
    private MessageHeaders headers;

    @Override
    public Object getPayload() {
        return this;
    }

    @Override
    public MessageHeaders getHeaders() {
        return this.headers; // todo
    }

    @Override
    public String toString() {
        return "SocketErrorMessage{" +
                "sessionId='" + sessionId + '\'' +
                ", key='" + key + '\'' +
                ", message='" + message + '\n' +
                ", headers=" + headers +'\n' +
                ", stackTrace='" + formatStackTrace(stackTrace) + '\'' +
                '}';
    }

    private String formatStackTrace(String stackTrace) {
        return stackTrace != null ? stackTrace.replace(",", ",\n") : null;
    }

    public String toStringForWebsocketMsg() {
        return "SocketErrorMessage{" +
                "sessionId='" + sessionId + '\'' +
                ", key='" + key + '\'' +
                ", message='" + message +
                '}';
    }
}
