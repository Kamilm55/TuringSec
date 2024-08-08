package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.payload.message.StringMessageInReportPayload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public interface IStompMessageInReportService {
    void sendTextMessageToReportRoom(String room, StringMessageInReportPayload strMessageInReportPayload, SimpMessageHeaderAccessor headerAccessor);

}
