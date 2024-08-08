package com.turingSecApp.turingSec.helper.entityHelper.messageInReport;

import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.exception.custom.UserMustBeSameWithReportUserException;
import com.turingSecApp.turingSec.model.entities.report.Report;

public interface IMessageInReportEntityHelper {
    void checkUserReport(Object authenticatedUser, Long reportId) throws UserMustBeSameWithReportUserException;
    void checkCompanyReport(Object authenticatedUser, Long reportId) throws UserMustBeSameWithReportUserException;
    Report findReportById(Long reportId) throws ResourceNotFoundException;
}
