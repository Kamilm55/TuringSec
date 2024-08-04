package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.payload.message.StringMessageInReportPayload;
import com.turingSecApp.turingSec.service.interfaces.IStompMessageInReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
public class StompController {

    private final IStompMessageInReportService stompMessageInReportService;

    @MessageMapping("/{room}/sendMessage") //  stompClient.send('/app/{room}/sendMessage', {}, JSON.stringify);
    public void sendTextMessageToReportRoom(
            @DestinationVariable String room,
            @Payload @Valid StringMessageInReportPayload strMessageInReportPayload) {
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
