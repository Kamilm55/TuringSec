package com.turingSecApp.turingSec.filter.websocket.custom;

import com.turingSecApp.turingSec.config.websocket.headerAccessorAdapter.StompHeaderAccessorAdapter;
import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.model.repository.report.ReportRepository;
import com.turingSecApp.turingSec.service.interfaces.ICommonMessageInReportService;
import com.turingSecApp.turingSec.exception.websocket.exceptionHandling.SocketExceptionHandler;
import com.turingSecApp.turingSec.util.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportRoomInterceptor  implements ChannelInterceptor {
    private final UtilService utilService;
    private final ICommonMessageInReportService commonMessageInReportService;
    private final ReportRepository reportRepository;
    private final SocketExceptionHandler socketExceptionHandler;

    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompHeaderAccessorAdapter accessorAdapter = new StompHeaderAccessorAdapter(accessor);

        Message<?> messageFromHandler = socketExceptionHandler.executeWithExceptionHandling(() -> {
            Object authenticatedUser = utilService.getAuthenticatedBaseUserForWebsocket();

            // Check if the message is a SUBSCRIBE | SEND
            if (StompCommand.SUBSCRIBE.equals(accessor.getCommand()) | StompCommand.SEND.equals(accessor.getCommand())) {
                // Common field for events
                String destination = getDestinationFromHeaders(message);

                // Subscription in Report Room if command is SUBSCRIBE -> contains "messagesInReport"
                handleSubscription(destination, accessor, authenticatedUser);

                // SEND in Report Room if command is SEND -> contains "sendMessageInReport"
                handleSend(destination, accessor, authenticatedUser);

            }
        }, accessorAdapter, message);

        // Return the original message if validation passes, if error occurs it sets message as exception message
        return messageFromHandler;
    }

    private void handleSend(String destination, StompHeaderAccessor accessor, Object authenticatedUser) {
        if (destination.contains("sendMessageInReport")) {
            String room = extractRoomFromSend(destination);

            Report report = reportRepository.findByRoom(room)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("Report not found with room -> %s", room)));

            if (StompCommand.SEND.equals(accessor.getCommand())) {
                commonMessageInReportService.checkUserOrCompanyReport(authenticatedUser, report.getId());
            }
        }
    }

    private void handleSubscription(String destination, StompHeaderAccessor accessor, Object authenticatedUser) {
        if (destination.contains("messagesInReport")) {
            String room = extractRoomFromSubscription(destination);

            Report report = reportRepository.findByRoom(room)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("Report not found with room %s", room)));

            if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                commonMessageInReportService.checkUserOrCompanyReport(authenticatedUser, report.getId());
            }
        }
    }


    // Utils
    private String getDestinationFromHeaders(Message<?> message) {
        Object nativeHeadersObj = message.getHeaders().get("nativeHeaders");
        if (nativeHeadersObj == null) {
            throw new IllegalArgumentException("No native headers found");
        }

        Map<String, Object> nativeHeaders = (Map<String, Object>) nativeHeadersObj;
        return getDestinationHeaderOfMessage(nativeHeaders);
    }

    private String getDestinationHeaderOfMessage(Map<String, Object> nativeHeaders) {
        List<String> destinationHeaders = (List<String>) nativeHeaders.get("destination");
        if (destinationHeaders != null && !destinationHeaders.isEmpty()) {
            return destinationHeaders.get(0);
        }
        throw new IllegalArgumentException("No destination header found");
    }


    public String extractRoomFromSubscription(String destination) {
        String[] parts = destination.split("/");
        if (parts.length >= 4 && "topic".equals(parts[1]) && "messagesInReport".equals(parts[3])) {
            return parts[2];
        }
        throw new IllegalArgumentException("Invalid destination format");
    }

    public String extractRoomFromSend(String destination) {
        String[] parts = destination.split("/");
        if (parts.length >= 4 && "app".equals(parts[1]) && "sendMessageInReport".equals(parts[3])) {
            return parts[2];
        }
        throw new IllegalArgumentException("Invalid destination format");
    }

}
