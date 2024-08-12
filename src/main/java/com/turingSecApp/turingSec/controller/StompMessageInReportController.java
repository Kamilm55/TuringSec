package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.payload.message.StringMessageInReportPayload;
import com.turingSecApp.turingSec.service.interfaces.IStompMessageInReportService;
import com.turingSecApp.turingSec.exception.websocket.exceptionHandling.SocketErrorMessage;
import com.turingSecApp.turingSec.util.UtilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
public class StompMessageInReportController {

    private final IStompMessageInReportService stompMessageInReportService;

    //TODO
    // -> tekce Admine icaze ver
    // get all reports
    // get all reports by company id
    //  get all reports by user id
    // token istemeye ehtiyac yoxdu, payload hecne, dto -> BaseResponse<List<ReportDTO>> -> company id , program id , userId(hansi hackerdi) , username of user

    //1. user report atannan sonra  ---> submitted - unreviewed
    // POST submitManualReport-da statusu submitted - unreviewed set et
    // POST submitCVSS-da statusu submitted - unreviewed set et

    // hacker hissesinde all( submitted underreview (accepted | rejected) -> assessed )
    // sirket hissesinde all(unreviewed,reviewed,assessed)

    // get all reports for hacker -> var -> getAllBugBountyReportsByUser
    // get submitted reports for hacker
    // get underreview reports for hacker
    // get accepted reports for hacker
    // get rejected reports for hacker
    // get assessed reports for hacker -> if accepted | rejected return

    // get all reports for company -> var -> getAllBugBountyReportsByCompany
    // get submitted reports for company
    // get unreviewed reports for company
    // get reviewed reports for company
    // get assessed reports for company

    // QUERY ILE ET -> EGER QUERY PARAM SEHVDISE ILLAGEAL ARGUMENT

    @MessageMapping("/{room}/sendMessageInReport") //  stompClient.send('/app/{room}/sendMessageInReport', {}, JSON.stringify);
    public void sendTextMessageToReportRoom(
            @DestinationVariable String room,
            @Payload @Valid StringMessageInReportPayload strMessageInReportPayload,
            SimpMessageHeaderAccessor headerAccessor
            ) {
         stompMessageInReportService.sendTextMessageToReportRoom(room,strMessageInReportPayload,headerAccessor);
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
