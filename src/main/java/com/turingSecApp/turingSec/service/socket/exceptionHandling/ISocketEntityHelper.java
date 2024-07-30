package com.turingSecApp.turingSec.service.socket.exceptionHandling;

import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.exception.custom.UserMustBeSameWithReportUserException;
import com.turingSecApp.turingSec.model.entities.report.Report;

public interface ISocketEntityHelper {
    void checkUserReport(Object authenticatedUser, Long reportId) throws UserMustBeSameWithReportUserException;
    void checkCompanyReport(Object authenticatedUser, Long reportId) throws UserMustBeSameWithReportUserException;
    Report findReportById(Long reportId) throws ResourceNotFoundException;
}
