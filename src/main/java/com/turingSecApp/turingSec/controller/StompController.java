package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.config.websocket.CustomWebsocketSecurityContext;
import com.turingSecApp.turingSec.payload.message.StringMessageInReportPayload;
import com.turingSecApp.turingSec.service.interfaces.IStompMessageInReportService;
import com.turingSecApp.turingSec.service.socket.exceptionHandling.SocketErrorMessage;
import com.turingSecApp.turingSec.service.user.UserDetailsServiceImpl;
import com.turingSecApp.turingSec.util.UtilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
public class StompController {

    private final IStompMessageInReportService stompMessageInReportService;
    private final UtilService utilService;

    @MessageMapping("/{room}/sendMessageInReport") //  stompClient.send('/app/{room}/sendMessageInReport', {}, JSON.stringify);
    public void sendTextMessageToReportRoom(
            @DestinationVariable String room,
            @Payload @Valid StringMessageInReportPayload strMessageInReportPayload,
            SimpMessageHeaderAccessor headerAccessor
            ) {
         stompMessageInReportService.sendTextMessageToReportRoom(room,strMessageInReportPayload,headerAccessor);
    }


    @MessageMapping("/{sessionId}/error")
//    @SendTo("/topic/{sessionId}/error")
    public void test2(
//            @DestinationVariable String room,
            @Payload SocketErrorMessage errorDetails){

//        System.out.println(room);
        System.out.println(errorDetails.toString());

    }


    // For testing websocket
    @MessageMapping("/{room}/sendMessage/test")
    @SendTo("/topic/messages/test")
    public StringMessageInReportPayload test2(@Payload StringMessageInReportPayload strMessageInReportPayload){
        System.out.println(strMessageInReportPayload);

        return strMessageInReportPayload;
    }
    @MessageMapping("/public")
    @SendTo("/topic/messages")
    public Map<String,String> test(@Payload Map<String,String> msgObj){
        return msgObj;
    }

    public void sendErrorMessage(String id, String s) {

    }
}
