package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.Request.ReportsByUserDTO;
import com.turingSecApp.turingSec.Request.ReportsByUserWithCompDTO;
import com.turingSecApp.turingSec.dao.entities.ReportsEntity;
import com.turingSecApp.turingSec.payload.BugBountyReportPayload;
import com.turingSecApp.turingSec.payload.BugBountyReportUpdatePayload;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface IBugBountyReportService {
    ReportsEntity getBugBountyReportById(Long id);
    void submitBugBountyReport(BugBountyReportPayload reportPayload,Long bugBountyProgramId);
    ReportsEntity updateBugBountyReport(Long id,BugBountyReportUpdatePayload bugBountyReportUpdatePayload);
    void deleteBugBountyReport(Long id);
    List<ReportsByUserWithCompDTO> getAllBugBountyReportsByUser();

    List<ReportsByUserDTO> getBugBountyReportsForCompanyPrograms();

}
