package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.Request.ReportsByUserDTO;
import com.turingSecApp.turingSec.Request.ReportsByUserWithCompDTO;
import com.turingSecApp.turingSec.dao.entities.BugBountyProgramEntity;
import com.turingSecApp.turingSec.dao.entities.CollaboratorEntity;
import com.turingSecApp.turingSec.dao.entities.CompanyEntity;
import com.turingSecApp.turingSec.dao.entities.ReportsEntity;
import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
import com.turingSecApp.turingSec.dao.repository.CollaboratorRepository;
import com.turingSecApp.turingSec.dao.repository.ProgramsRepository;
import com.turingSecApp.turingSec.dao.repository.ReportsRepository;
import com.turingSecApp.turingSec.dao.repository.UserRepository;
import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.exception.custom.UnauthorizedException;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.payload.BugBountyReportPayload;
import com.turingSecApp.turingSec.payload.BugBountyReportUpdatePayload;
import com.turingSecApp.turingSec.response.CollaboratorDTO;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.service.BugBountyReportService;
import com.turingSecApp.turingSec.service.interfaces.IBugBountyReportService;
import com.turingSecApp.turingSec.service.user.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
