package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.dao.entities.report.ReportCVSS;
import com.turingSecApp.turingSec.dao.entities.report.ReportManual;
import com.turingSecApp.turingSec.payload.report.BugBountyReportPayload;
import com.turingSecApp.turingSec.payload.report.ReportCVSSPayload;
import com.turingSecApp.turingSec.payload.report.ReportManualPayload;
import com.turingSecApp.turingSec.response.report.ReportDTO;
import com.turingSecApp.turingSec.response.report.ReportsByUserDTO;
import com.turingSecApp.turingSec.response.report.ReportsByUserWithCompDTO;

import java.util.List;

public interface IBugBountyReportService {
    ReportDTO getBugBountyReportById(Long id);
//    ReportDTO submitBugBountyReport(BugBountyReportPayload reportPayload, Long bugBountyProgramId);
//    ReportDTO updateBugBountyReport(Long id,BugBountyReportPayload bugBountyReportUpdatePayload);
    void deleteBugBountyReport(Long id);
    List<ReportsByUserWithCompDTO> getAllBugBountyReportsByUser();

    List<ReportsByUserDTO> getBugBountyReportsForCompanyPrograms();

    // Manual
    ReportManual submitManualReport(ReportManualPayload reportPayload, Long bugBountyProgramId);
    ReportManual updateManualReport(Long id, ReportManualPayload reportPayload);

    // CVSSReport
    ReportCVSS submitCVSSReport(ReportCVSSPayload reportPayload, Long bugBountyProgramId);

    ReportCVSS updateCVSSReport(Long id, ReportCVSSPayload bugBountyReportUpdatePayload);

    //
}
