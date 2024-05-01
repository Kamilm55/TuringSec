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
import com.turingSecApp.turingSec.service.user.CustomUserDetails;
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

    private final UserRepository userRepository;
    private final ProgramsRepository programsRepository;
    private final BugBountyReportService bugBountyReportService;
    private final ReportsRepository bugBountyReportRepository;
    private final CollaboratorRepository collaboratorRepository;


//    @GetMapping// No need , because every report belongs to specific hacker or company
//    public ResponseEntity<List<ReportsEntity>> getAllBugBountyReports() {
//        List<ReportsEntity> bugBountyReports = bugBountyReportService.getAllBugBountyReports();
//        return new ResponseEntity<>(bugBountyReports, HttpStatus.OK);
//    }

    @GetMapping("/{id}")
    public BaseResponse<ReportsEntity> getBugBountyReportById(@PathVariable Long id) {
        ReportsEntity bugBountyReport = bugBountyReportService.getBugBountyReportById(id);
        return BaseResponse.success(bugBountyReport);
    }

    @PostMapping("/submit")
    public BaseResponse<?> submitBugBountyReport(@RequestBody BugBountyReportPayload reportPayload, @RequestParam Long bugBountyProgramId) {
       return bugBountyReportService.submitBugBountyReport(reportPayload,bugBountyProgramId);
    }

    @PutMapping("/{id}")
    public BaseResponse<ReportsEntity> updateBugBountyReport(@PathVariable Long id,
                                                                  @RequestBody BugBountyReportUpdatePayload bugBountyReportUpdatePayload) {
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
        List<ReportsByUserWithCompDTO> userReports = bugBountyReportService.getAllReportsByUser();
        return BaseResponse.success(userReports);
    }

    @GetMapping("/reports/company")
    public BaseResponse<List<ReportsByUserDTO>> getBugBountyReportsForCompanyPrograms() {
        // Retrieve the authenticated user details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Extract the company from the authenticated user details
        Object user = userDetails.getUser();
        CompanyEntity company = null;
        if (user instanceof CompanyEntity) {
            company = (CompanyEntity) user;
        } else {
            // Handle the case where the user is not a company (e.g., throw an exception or return an error response)
            // For example:
            throw new IllegalStateException("Authenticated user is not a company");
        }

        // Retrieve bug bounty reports submitted for the company's programs
        List<ReportsByUserDTO> reportsForCompanyPrograms = bugBountyReportService.getBugBountyReportsForCompanyPrograms(company);

        return BaseResponse.success(reportsForCompanyPrograms);
    }
}
