package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.response.message.StringMessageInReportDTO;
import com.turingSecApp.turingSec.response.report.AllReportDTO;
import com.turingSecApp.turingSec.response.report.ReportDTO;

import java.util.List;

public interface IMessageInReportService {

    StringMessageInReportDTO getMessageById(Long id);

    List<StringMessageInReportDTO> getMessagesByRoom(String room);


    List<StringMessageInReportDTO> getMessageByReportId(Long reportId);

    StringMessageInReportDTO getMessageWithId(Long id);

    List<Report> getReportsByCompanyId(Long companyId);

    List<Report> getReportsByUserId(Long userId);

    List<Report> getAllReports();


//    List<AllReportDTO> getAllReports();

}

