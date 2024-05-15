package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.dao.entities.report.ReportCVSS;
import com.turingSecApp.turingSec.dao.entities.report.ReportManual;
import com.turingSecApp.turingSec.payload.report.ReportCVSSPayload;
import com.turingSecApp.turingSec.payload.report.ReportManualPayload;
import com.turingSecApp.turingSec.response.report.ReportDTO;
import com.turingSecApp.turingSec.response.report.ReportsByUserDTO;
import com.turingSecApp.turingSec.response.report.ReportsByUserWithCompDTO;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.service.interfaces.IBugBountyReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bug-bounty-reports")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class BugBountyReportController {

    private final IBugBountyReportService bugBountyReportService;

    @GetMapping("/{id}")
    public BaseResponse<ReportDTO> getBugBountyReportById(@PathVariable Long id) {
        ReportDTO bugBountyReport = bugBountyReportService.getBugBountyReportById(id);
        return BaseResponse.success(bugBountyReport);
    }

    @PostMapping("/manualReport")
    public BaseResponse<ReportManual> submitManualReport(@RequestBody @Valid ReportManualPayload reportPayload, @RequestParam Long bugBountyProgramId) {
        ReportManual submittedBugBountyReport = bugBountyReportService.submitManualReport(reportPayload, bugBountyProgramId);
        return BaseResponse.success(submittedBugBountyReport,"Bug bounty report submitted successfully");
    }

    @PutMapping("/manualReport/{id}")
    public BaseResponse<ReportManual> updateManualReport(@PathVariable Long id,
                                                         @RequestBody @Valid ReportManualPayload bugBountyReportUpdatePayload) {
        ReportManual updatedReport = bugBountyReportService.updateManualReport(id, bugBountyReportUpdatePayload);
        return BaseResponse.success(updatedReport);
    }

    //
    @PostMapping("/CVSSReport")
    public BaseResponse<ReportCVSS> submitManualReport(@RequestBody @Valid ReportCVSSPayload reportPayload, @RequestParam Long bugBountyProgramId) {
        ReportCVSS submittedBugBountyReport = bugBountyReportService.submitCVSSReport(reportPayload, bugBountyProgramId);
        return BaseResponse.success(submittedBugBountyReport,"Bug bounty report submitted successfully");
    }

    @PutMapping("/CVSSReport/{id}")
    public BaseResponse<ReportCVSS> updateManualReport(@PathVariable Long id,
                                                         @RequestBody @Valid ReportCVSSPayload bugBountyReportUpdatePayload) {
        ReportCVSS updatedReport = bugBountyReportService.updateCVSSReport(id, bugBountyReportUpdatePayload);
        return BaseResponse.success(updatedReport);
    }


    @DeleteMapping("/{id}")
    public BaseResponse<?> deleteBugBountyReport(@PathVariable Long id) {
        bugBountyReportService.deleteBugBountyReport(id);
        // refactorThis -> new ResponseEntity<>(HttpStatus.NO_CONTENT)
        return BaseResponse.success();
    }

    @GetMapping("/user")
    public BaseResponse<List<ReportsByUserWithCompDTO>> getAllBugBountyReportsByUser() {
        List<ReportsByUserWithCompDTO> userReports = bugBountyReportService.getAllBugBountyReportsByUser();
        return BaseResponse.success(userReports);
    }

    @GetMapping("/reports/company")
    public BaseResponse<List<ReportsByUserDTO>> getBugBountyReportsForCompanyPrograms() {
        return BaseResponse.success(bugBountyReportService.getBugBountyReportsForCompanyPrograms());
    }

    //    @GetMapping// No need , because every report belongs to specific hacker or company
//    public ResponseEntity<List<ReportsEntity>> getAllBugBountyReports() {
//        List<ReportsEntity> bugBountyReports = bugBountyReportService.getAllBugBountyReports();
//        return new ResponseEntity<>(bugBountyReports, HttpStatus.OK);
//    }
}
