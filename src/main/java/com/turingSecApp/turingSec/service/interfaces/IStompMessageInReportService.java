package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.payload.message.StringMessageInReportPayload;

public interface IStompMessageInReportService {
    void sendTextMessageToReportRoom(String room, StringMessageInReportPayload strMessageInReportPayload);

}
