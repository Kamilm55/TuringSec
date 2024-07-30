package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.response.message.StringMessageInReportDTO;

import java.util.List;

public interface IMessageInReportService {

    StringMessageInReportDTO getMessageById(Long id);

    List<StringMessageInReportDTO> getMessagesByRoom(String room);


    List<StringMessageInReportDTO> getMessageByReportId(Long reportId);

    StringMessageInReportDTO getMessageWithId(Long id);
}

