package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.response.ReportsByUserDTO;
import com.turingSecApp.turingSec.response.ReportsByUserWithCompDTO;
import com.turingSecApp.turingSec.dao.entities.ReportsEntity;
import com.turingSecApp.turingSec.payload.BugBountyReportPayload;
import com.turingSecApp.turingSec.payload.BugBountyReportUpdatePayload;
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
    public BaseResponse<ReportsEntity> getBugBountyReportById(@PathVariable Long id) {
        ReportsEntity bugBountyReport = bugBountyReportService.getBugBountyReportById(id);
        return BaseResponse.success(bugBountyReport);
    }

    @PostMapping("/submit")
    public BaseResponse<?> submitBugBountyReport(@RequestBody @Valid BugBountyReportPayload reportPayload, @RequestParam Long bugBountyProgramId) {
        bugBountyReportService.submitBugBountyReport(reportPayload,bugBountyProgramId);
       return BaseResponse.success(null,"Bug bounty report submitted successfully");
    }

    @PutMapping("/{id}")
    public BaseResponse<ReportsEntity> updateBugBountyReport(@PathVariable Long id,
                                                                  @RequestBody @Valid BugBountyReportUpdatePayload bugBountyReportUpdatePayload) {
        ReportsEntity updatedReport = bugBountyReportService.updateBugBountyReport(id, bugBountyReportUpdatePayload);
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
