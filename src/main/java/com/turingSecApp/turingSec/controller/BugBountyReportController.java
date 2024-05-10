package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.payload.report.BugBountyReportPayload;
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

    @PostMapping("/submit")
    public BaseResponse<ReportDTO> submitBugBountyReport(@RequestBody @Valid BugBountyReportPayload reportPayload, @RequestParam Long bugBountyProgramId) {
        ReportDTO submittedBugBountyReport = bugBountyReportService.submitBugBountyReport(reportPayload, bugBountyProgramId);
        return BaseResponse.success(submittedBugBountyReport,"Bug bounty report submitted successfully");
    }

    @PutMapping("/{id}")
    public BaseResponse<ReportDTO> updateBugBountyReport(@PathVariable Long id,
                                                                  @RequestBody @Valid BugBountyReportPayload bugBountyReportUpdatePayload) {
        ReportDTO updatedReport = bugBountyReportService.updateBugBountyReport(id, bugBountyReportUpdatePayload);
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
