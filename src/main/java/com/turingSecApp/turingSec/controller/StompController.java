package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.payload.message.StringMessageInReportPayload;
import com.turingSecApp.turingSec.service.interfaces.IStompMessageInReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class StompController {

    private final IStompMessageInReportService stompMessageInReportService;

    @MessageMapping("/{room}/sendMessage") //  stompClient.send('/app/{room}/sendMessage', {}, 'Hello, World!');
    public void sendTextMessageToReportRoom(@PathVariable String room, @Payload @Valid StringMessageInReportPayload strMessageInReportPayload){
        stompMessageInReportService.sendTextMessageToReportRoom(room,strMessageInReportPayload);
    }

    @MessageMapping("/public")
    @SendTo("/topic/messages")
    public String test(){
        return "Works for test in public ws";
    }
}
