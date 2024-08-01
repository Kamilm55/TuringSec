package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.payload.message.StringMessageInReportPayload;
import com.turingSecApp.turingSec.service.interfaces.IStompMessageInReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class StompController {

    private final IStompMessageInReportService stompMessageInReportService;

    @MessageMapping("/{room}/sendMessage") //  stompClient.send('/app/{room}/sendMessage', {}, JSON.stringify);
    public void sendTextMessageToReportRoom(@DestinationVariable String room, @Payload @Valid StringMessageInReportPayload strMessageInReportPayload){
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
