package com.turingSecApp.turingSec.exception.websocket.exceptionHandling;

import com.turingSecApp.turingSec.config.websocket.headerAccessorAdapter.CustomHeaderAccessor;
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
    // Note: Any of websocket component from comes websocket library cannot be injected into this class -> it creates circular flow

    // For Interceptors
    public Message<?> executeWithExceptionHandling(Runnable action, CustomHeaderAccessor accessor,Message<?> messageFromInterceptor) throws Exception  {
        String sessionId = accessor.getSessionId();

        // Get singleton error obj
        SocketErrorMessage socketErrorMessage = SocketErrorMessageSingleton.getInstance();

        try {
            action.run();
        }
        catch (UserMustBeSameWithReportUserException | UnauthorizedException customEx){
            handleExceptionAndThrowExceptionAgain(accessor, messageFromInterceptor, customEx, socketErrorMessage, sessionId);
        }
        catch (Exception ex) {
            log.error("Unhandled ex in socket exHandler!");
            handleExceptionAndThrowExceptionAgain(accessor, messageFromInterceptor, ex, socketErrorMessage, sessionId);
        }

        // After closing socket I must set key to null
        if(socketErrorMessage.getKey() == null ){
            return messageFromInterceptor;
        }

        // If socketErrorMessage key is not null, it means there is an error
        return socketErrorMessage;
    }

    private void handleExceptionAndThrowExceptionAgain(CustomHeaderAccessor accessor, Message<?> messageFromInterceptor, Exception ex, SocketErrorMessage socketErrorMessage, String sessionId) {
        // For handle exception by my custom logic
        handleException(socketErrorMessage, ex, sessionId, accessor, messageFromInterceptor);

        // Throw exception again -> Websocket Exception handler handles this and close socket (which I also customize before closing logic)
        throw new RuntimeException(ex.getMessage());
    }

    //todo: For service methods
    public void executeWithExceptionHandling(Runnable action, CustomHeaderAccessor accessor, SimpMessagingTemplate messagingTemplate) {
        String sessionId = accessor.getSessionId();

        try {
            action.run();
        }
        catch (UserMustBeSameWithReportUserException | UnauthorizedException customEx){
//            handleException(customEx,sessionId);

            //todo: @MessageMapping("/{sessionId}/error") -> bura erroru gonder

//            messagingTemplate.convertAndSend("topic/error");
            // Disconnect the client (direct disconnect is not possible in stomp)
//            messagingTemplate.convertAndSend(String.format("/topic/%s/close",sessionId),"You must close socket with session id:" + sessionId);

        }
        catch (Exception ex) {
//            handleException(ex, sessionId);
        }

    }

    private void handleException(SocketErrorMessage socketErrorMessage,Exception ex, String sessionId,CustomHeaderAccessor accessor,Message<?> message) {
        // Populate error details before log and send as an event
        populateErrorDetails(socketErrorMessage,ex,sessionId);

        socketErrorMessage.setHeaders(message.getHeaders());

        accessor.setDestination("/topic/error");

        log.error("Error in socket: " + socketErrorMessage);
    }


    private void populateErrorDetails(SocketErrorMessage socketErrorMessage,Exception ex, String sessionId){
        // Create a detailed error message
        String key = ex.getClass().getName();
        String message = ex.getMessage();
        String stackTrace = Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));

        // Create a structured error object
        socketErrorMessage.setKey(key);
        socketErrorMessage.setSessionId(sessionId);
        socketErrorMessage.setMessage(message);
        socketErrorMessage.setStackTrace(stackTrace);

    }

}
