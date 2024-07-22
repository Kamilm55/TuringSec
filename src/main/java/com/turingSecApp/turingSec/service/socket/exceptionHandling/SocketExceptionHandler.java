package com.turingSecApp.turingSec.service.socket.exceptionHandling;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.turingSecApp.turingSec.exception.custom.UnauthorizedException;
import com.turingSecApp.turingSec.exception.custom.UserMustBeSameWithReportUserException;
import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.model.repository.CompanyRepository;
import com.turingSecApp.turingSec.model.repository.program.ProgramRepository;
import com.turingSecApp.turingSec.model.repository.report.ReportsRepository;
import com.turingSecApp.turingSec.model.repository.reportMessage.BaseMessageInReportRepository;
import com.turingSecApp.turingSec.model.repository.reportMessage.StringMessageInReportRepository;
import com.turingSecApp.turingSec.response.message.StringMessageInReportDTO;
import com.turingSecApp.turingSec.service.socket.SocketService;
import com.turingSecApp.turingSec.service.user.UserDetailsServiceImpl;
import com.turingSecApp.turingSec.util.UtilService;
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



//        ThrowingConsumer<SocketIOClient> action // it can be used
    public void executeWithExceptionHandling(Runnable action, SocketIOClient socketIOClient) {
        try {
            action.run();
        }
        catch (UserMustBeSameWithReportUserException | UnauthorizedException customEx){
            handleException(customEx, socketIOClient);

            // Disconnect the client
            socketIOClient.disconnect();
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
        log.error(String.format("Error Details: key: %s, message: %s, \n stackTrace: %s", errorDetails.get("key"),errorDetails.get("message"),errorDetails.get("stackTrace")));
    }

    private void sendErrorEvent(SocketIOClient socketIOClient) {
        // Custom event sending implementation

        // Send the error event for only own user without room, because error can be thrown before joining room
        // this error only visible for who sent
        socketIOClient.getNamespace().getAllClients().forEach(x -> {
            if (x.getSessionId().equals(socketIOClient.getSessionId())) {
                log.warn("log works inside  exhandler: "  );
                x.sendEvent("error", errorDetails);
            }
        });

    }

    private void populateErrorDetails(Exception ex){
        // Create a detailed error message
        String key = ex.getClass().getName();
        String message = ex.getMessage();
        String stackTrace = Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));

        // Create a structured error object
        errorDetails.put("stackTrace", stackTrace);
        errorDetails.put("message", message);
        errorDetails.put("key", key);
    }

}
