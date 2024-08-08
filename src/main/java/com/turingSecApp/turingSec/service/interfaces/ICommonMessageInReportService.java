package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.model.entities.message.StringMessageInReport;
import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.payload.message.StringMessageInReportPayload;
import com.turingSecApp.turingSec.response.message.StringMessageInReportDTO;

public interface ICommonMessageInReportService {
    void checkUserOrCompanyReport(Object authenticatedUser, Long reportId);
    StringMessageInReport createStringMessageInReport(StringMessageInReportPayload data, Object authenticatedUser, Report reportOfMessage);
    StringMessageInReportDTO toStringMessageInReportDTO(StringMessageInReport savedMsg);
}
