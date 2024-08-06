package com.turingSecApp.turingSec.service.socket.exceptionHandling;

import com.turingSecApp.turingSec.config.websocket.SocketErrorMessageSingleton;
import com.turingSecApp.turingSec.config.websocket.adapter.CustomHeaderAccessor;
import com.turingSecApp.turingSec.exception.custom.UnauthorizedException;
import com.turingSecApp.turingSec.exception.custom.UserMustBeSameWithReportUserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SocketExceptionHandler {


    public Message<?> executeWithExceptionHandling(Runnable action, CustomHeaderAccessor accessor,Message<?> messageFromInterceptor) throws Exception  {
        String sessionId = accessor.getSessionId();

        System.out.println("sesion id: " + sessionId);

        try {
            action.run();
        }
        catch (UserMustBeSameWithReportUserException | UnauthorizedException customEx){
            handleException(customEx,sessionId,accessor,messageFromInterceptor);

            throw new RuntimeException(customEx.getMessage());
        }
        catch (Exception ex) {
            log.error("Unhandled ex in socket exHandler!");
            handleException(ex,sessionId,accessor,messageFromInterceptor);
            throw new RuntimeException(ex.getMessage());
        }

        SocketErrorMessage socketErrorMessage = SocketErrorMessageSingleton.getInstance();

        System.out.println("AT the end socketErrorMessage:" + socketErrorMessage);

        if(socketErrorMessage.getKey() == null ){
            return messageFromInterceptor;
        }
        return socketErrorMessage;
    }

    public void executeWithExceptionHandling(Runnable action, CustomHeaderAccessor accessor, SimpMessagingTemplate messagingTemplate) {
        String sessionId = accessor.getSessionId();

        try {
            action.run();
        }
        catch (UserMustBeSameWithReportUserException | UnauthorizedException customEx){
            handleException(customEx,sessionId);

            //todo: @MessageMapping("/{sessionId}/error") -> bura erroru gonder

            // Disconnect the client (direct disconnect is not possible in stomp)
//            messagingTemplate.convertAndSend(String.format("/topic/%s/close",sessionId),"You must close socket with session id:" + sessionId);

        }
        catch (Exception ex) {
            handleException(ex, sessionId);
        }

    }

    private void handleException(Exception ex, String sessionId,CustomHeaderAccessor accessor,Message<?> message) {
        // Populate error details before log and send as an event
        populateErrorDetails(ex,sessionId);

        SocketErrorMessage socketErrorMessage = SocketErrorMessageSingleton.getInstance();
        socketErrorMessage.setHeaders(message.getHeaders());
//        accessor.setDestination(String.format("/topic/%s/error",sessionId));

        accessor.setDestination("/topic/error");

        log.error("Error in socket: " + socketErrorMessage.toString());
    }
    private void handleException(Exception ex, String sessionId) {
        // Populate error details before log and send as an event
        populateErrorDetails(ex,sessionId);

        SocketErrorMessage socketErrorMessage = SocketErrorMessageSingleton.getInstance();

        log.error("Error in socket: " + socketErrorMessage.toString());
    }



    private void populateErrorDetails(Exception ex, String sessionId){
        // Create a detailed error message
        String key = ex.getClass().getName();
        String message = ex.getMessage();
        String stackTrace = Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));

        // Create a structured error object
        SocketErrorMessage socketErrorMessage = SocketErrorMessageSingleton.getInstance();
        socketErrorMessage.setKey(key);
        socketErrorMessage.setSessionId(sessionId);
        socketErrorMessage.setMessage(message);
        socketErrorMessage.setStackTrace(stackTrace);

    }

}
