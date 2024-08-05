package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.config.websocket.CustomWebsocketSecurityContext;
import com.turingSecApp.turingSec.payload.message.StringMessageInReportPayload;
import com.turingSecApp.turingSec.service.interfaces.IStompMessageInReportService;
import com.turingSecApp.turingSec.service.user.UserDetailsServiceImpl;
import com.turingSecApp.turingSec.util.UtilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
public class StompController {

    private final IStompMessageInReportService stompMessageInReportService;
    private final UtilService utilService;

    @MessageMapping("/{room}/sendMessage") //  stompClient.send('/app/{room}/sendMessage', {}, JSON.stringify);
    public void sendTextMessageToReportRoom(
            @DestinationVariable String room,
            @Payload @Valid StringMessageInReportPayload strMessageInReportPayload) {
        Object authenticatedBaseUser = utilService.getAuthenticatedBaseUserForWebsocket();

        System.out.println("authenticatedBaseUser: " + authenticatedBaseUser);

//        System.out.println("user details:" + userDetails);
         stompMessageInReportService.sendTextMessageToReportRoom(room,strMessageInReportPayload);
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
}
