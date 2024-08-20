package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.model.entities.report.ReportCVSS;
import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.model.entities.report.ReportManual;
import com.turingSecApp.turingSec.model.entities.report.enums.REPORTSTATUSFORCOMPANY;
import com.turingSecApp.turingSec.model.entities.report.enums.REPORTSTATUSFORUSER;
import com.turingSecApp.turingSec.payload.report.ReportCVSSPayload;
import com.turingSecApp.turingSec.payload.report.ReportManualPayload;
import com.turingSecApp.turingSec.response.report.ReportsByUserDTO;
import com.turingSecApp.turingSec.response.report.ReportsByUserWithCompDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IReportService {
    Report getBugBountyReportById(Long id);
    void deleteBugBountyReport(Long id);

    // Manual
    ReportManual submitManualReportForTest(List<MultipartFile> files,ReportManualPayload reportPayload, Long bugBountyProgramId) throws IOException;

    ReportManual submitManualReport(List<MultipartFile> files,UserDetails userDetails, ReportManualPayload reportPayload, Long bugBountyProgramId) throws IOException;

    // CVSSReport
    ReportCVSS submitCVSSReport(List<MultipartFile> files, UserDetails userDetails,ReportCVSSPayload reportPayload, Long bugBountyProgramId) throws IOException;

    //
    List<Report> getReportsByCompanyId(Long companyId);

    List<Report> getReportsByUserId(Long userId);

    List<Report> getAllReports();

    Report reviewReportByCompany(Long id);

    Report acceptReportByCompany(Long id);

    Report rejectReportByCompany(Long id);

    List<ReportsByUserWithCompDTO> getReportsByUserWithStatus(REPORTSTATUSFORUSER status);

    List<ReportsByUserDTO> getReportsByCompanyProgramWithStatus(REPORTSTATUSFORCOMPANY status);
}
