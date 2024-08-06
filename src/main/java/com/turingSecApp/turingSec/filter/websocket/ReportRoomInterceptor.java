package com.turingSecApp.turingSec.filter.websocket;

import com.turingSecApp.turingSec.config.websocket.adapter.StompHeaderAccessorAdapter;
import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.exception.custom.UnauthorizedException;
import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.model.repository.report.ReportRepository;
import com.turingSecApp.turingSec.service.socket.SocketEntityHelper;
import com.turingSecApp.turingSec.service.socket.exceptionHandling.SocketExceptionHandler;
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
    private final SocketEntityHelper socketEntityHelper;
    private final ReportRepository reportRepository;
    private final SocketExceptionHandler socketExceptionHandler;

//    private final CustomMessagingService customMessagingService;

    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompHeaderAccessorAdapter accessorAdapter = new StompHeaderAccessorAdapter(accessor);

        Message<?> errMessage = socketExceptionHandler.executeWithExceptionHandling( () -> {
            Object authenticatedUser = utilService.getAuthenticatedBaseUserForWebsocket();


            if(2+2==4)throw new RuntimeException("Rand ex");
            // todo: FunctionalInterface for this if and send error event
            // Check if the message is a CONNECT | SUBSCRIBE | SEND
            if (/*StompCommand.CONNECT.equals(accessor.getCommand())  | */StompCommand.SUBSCRIBE.equals(accessor.getCommand()) | StompCommand.SEND.equals(accessor.getCommand())) {
                // Common fields for events

                log.info("Message" + message);

            String destination = getDestinationFromHeaders(message);
            String room = extractRoomFromSubscription(destination);

            Report report = reportRepository.findByRoom(room)
                    .orElseThrow(() -> new ResourceNotFoundException("Report not found with room: " + room));

            if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                handleSubscription(authenticatedUser, report);
            } else if (StompCommand.SEND.equals(accessor.getCommand())) {
                handleSend(authenticatedUser, report);
            }

//                if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
//                    String sessionId = accessor.getSessionId();
//
//                    // Here you might want to notify or log the disconnection event
//                    customMessagingService.sendMessage(
//                            String.format("/topic/%s/notification",sessionId),
//                            "You have been disconnected"
//                    );
//                }
            }
        },accessorAdapter,message);
//
//        if (errMessage != null) {
//            return errMessage;
//        }
            return message;
    }

    private void handleSubscription(Object authenticatedUser, Report report) {
        checkUserOrCompanyReport(authenticatedUser, report.getId());
    }

    private void handleSend(Object authenticatedUser, Report report) {
        checkUserOrCompanyReport(authenticatedUser, report.getId());
    }

    private void checkUserOrCompanyReport(Object authenticatedUser, Long reportId) {
        if (authenticatedUser instanceof UserEntity) {
            socketEntityHelper.checkUserReport(authenticatedUser, reportId);
        } else if (authenticatedUser instanceof CompanyEntity) {
            socketEntityHelper.checkCompanyReport(authenticatedUser, reportId);
        } else {
            throw new UnauthorizedException("User is neither Hacker nor Company!");
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
        if (parts.length >= 4 && "app".equals(parts[1]) && "sendMessage".equals(parts[3])) {
            return parts[2];
        }
        throw new IllegalArgumentException("Invalid destination format");
    }




}
