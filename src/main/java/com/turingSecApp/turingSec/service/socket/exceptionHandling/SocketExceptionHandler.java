package com.turingSecApp.turingSec.service.socket.exceptionHandling;

import com.corundumstudio.socketio.SocketIOClient;
import com.turingSecApp.turingSec.exception.custom.UserMustBeSameWithReportUserException;
import com.turingSecApp.turingSec.response.message.StringMessageInReportDTO;
import com.turingSecApp.turingSec.util.mapper.StringMessageInReportMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SocketExceptionHandler {

    private Map<String, Object> errorDetails = new HashMap<>();

//    ThrowingConsumer<SocketIOClient> action // it can be used
    public void executeWithExceptionHandling(Runnable action, SocketIOClient socketIOClient) {
        try {
            action.run();
        }
        catch (UserMustBeSameWithReportUserException customEx){
            // Disconnect the client
            socketIOClient.disconnect();

            log.error("UserMustBeSameWithReportUserException has caught");
            handleException(customEx, socketIOClient);
        }
        catch (Exception ex) {
            handleException(ex, socketIOClient);
        }
    }

    private void handleException(Exception ex, SocketIOClient socketIOClient) {
        // Populate error details before log and send as an event
        populateErrorDetails(ex);

        logError();
        sendErrorEvent(socketIOClient);
    }

    private void logError() {
        // Custom logging implementation
        log.error(errorDetails.toString());
    }

    private void sendErrorEvent(SocketIOClient socketIOClient) {
        // Custom event sending implementation

        // Room from path query (urlParam) and get Report from room
        String room = socketIOClient.getHandshakeData().getSingleUrlParam("room");

        // Send the error event for only own user
        socketIOClient.getNamespace().getRoomOperations(room).getClients().forEach(x -> {
            if (x.getSessionId().equals(socketIOClient.getSessionId())) {
                x.sendEvent("error", errorDetails);
            }
        });

    }

    private void populateErrorDetails(Exception ex){
        // Create a detailed error message
        String errorClass = ex.getClass().getName();
        String errorMessage = ex.getMessage();
        String stackTrace = Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));

        // Create a structured error object
        errorDetails.put("errorClass", errorClass);
        errorDetails.put("errorMessage", errorMessage);
        errorDetails.put("stackTrace", stackTrace);
    }

}
