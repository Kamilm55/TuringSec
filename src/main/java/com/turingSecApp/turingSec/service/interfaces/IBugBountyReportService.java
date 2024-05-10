package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.payload.report.BugBountyReportPayload;
import com.turingSecApp.turingSec.response.report.ReportDTO;
import com.turingSecApp.turingSec.response.report.ReportsByUserDTO;
import com.turingSecApp.turingSec.response.report.ReportsByUserWithCompDTO;

import java.util.List;

public interface IBugBountyReportService {
    ReportDTO getBugBountyReportById(Long id);
    ReportDTO submitBugBountyReport(BugBountyReportPayload reportPayload, Long bugBountyProgramId);
    ReportDTO updateBugBountyReport(Long id,BugBountyReportPayload bugBountyReportUpdatePayload);
    void deleteBugBountyReport(Long id);
    List<ReportsByUserWithCompDTO> getAllBugBountyReportsByUser();

    List<ReportsByUserDTO> getBugBountyReportsForCompanyPrograms();

}
