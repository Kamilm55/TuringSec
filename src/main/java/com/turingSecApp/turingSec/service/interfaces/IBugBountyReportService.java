package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.response.ReportsByUserDTO;
import com.turingSecApp.turingSec.response.ReportsByUserWithCompDTO;
import com.turingSecApp.turingSec.dao.entities.ReportsEntity;
import com.turingSecApp.turingSec.payload.BugBountyReportPayload;
import com.turingSecApp.turingSec.payload.BugBountyReportUpdatePayload;

import java.util.List;

public interface IBugBountyReportService {
    ReportsEntity getBugBountyReportById(Long id);
    void submitBugBountyReport(BugBountyReportPayload reportPayload,Long bugBountyProgramId);
    ReportsEntity updateBugBountyReport(Long id,BugBountyReportUpdatePayload bugBountyReportUpdatePayload);
    void deleteBugBountyReport(Long id);
    List<ReportsByUserWithCompDTO> getAllBugBountyReportsByUser();

    List<ReportsByUserDTO> getBugBountyReportsForCompanyPrograms();

}
