package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.model.entities.report.ReportCVSS;
import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.model.entities.report.ReportManual;
import com.turingSecApp.turingSec.payload.report.ReportCVSSPayload;
import com.turingSecApp.turingSec.payload.report.ReportManualPayload;
import com.turingSecApp.turingSec.response.report.ReportsByUserDTO;
import com.turingSecApp.turingSec.response.report.ReportsByUserWithCompDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IBugBountyReportService {
    Report getBugBountyReportById(Long id);
//    ReportDTO submitBugBountyReport(BugBountyReportPayload reportPayload, Long bugBountyProgramId);
//    ReportDTO updateBugBountyReport(Long id,BugBountyReportPayload bugBountyReportUpdatePayload);
    void deleteBugBountyReport(Long id);
    List<ReportsByUserWithCompDTO> getAllBugBountyReportsByUser();

    List<ReportsByUserDTO> getBugBountyReportsForCompanyPrograms();

    // Manual
    ReportManual submitManualReport(List<MultipartFile> files,UserDetails userDetails, ReportManualPayload reportPayload, Long bugBountyProgramId) throws IOException;
    ReportManual updateManualReport(Long id, ReportManualPayload reportPayload);

    // CVSSReport
    ReportCVSS submitCVSSReport(List<MultipartFile> files, UserDetails userDetails,ReportCVSSPayload reportPayload, Long bugBountyProgramId) throws IOException;

    ReportCVSS updateCVSSReport(Long id, ReportCVSSPayload bugBountyReportUpdatePayload);

    //
}
