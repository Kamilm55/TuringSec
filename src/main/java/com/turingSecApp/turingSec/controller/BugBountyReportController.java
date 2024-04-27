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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Fetch the BugBountyProgramEntity from the repository
        BugBountyProgramEntity program = programsRepository.findById(bugBountyProgramId)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id:" + bugBountyProgramId));

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));

            ReportsEntity report = new ReportsEntity();

            // Populate report entity
            report.setAsset(reportPayload.getAsset());
            report.setWeakness(reportPayload.getWeakness());
            report.setSeverity(reportPayload.getSeverity());
            report.setMethodName(reportPayload.getMethodName());
            report.setProofOfConcept(reportPayload.getProofOfConcept());
            report.setDiscoveryDetails(reportPayload.getDiscoveryDetails());
            report.setLastActivity(reportPayload.getLastActivity());
            report.setReportTitle(reportPayload.getReportTitle());
            report.setRewardsStatus(reportPayload.getRewardsStatus());
            report.setVulnerabilityUrl(reportPayload.getVulnerabilityUrl());

            // Set the user for the bug bounty report
            UserEntity userFromDB = userRepository.findById(reportPayload.getUserId()).orElseThrow(() -> new UserNotFoundException("User with id " + reportPayload.getUserId() + " not found"));
            report.setUser(userFromDB);

            // Set the bug bounty program for the bug bounty report
            report.setBugBountyProgram(program);

            ReportsEntity saved = bugBountyReportRepository.save(report);

            ReportsEntity reportFromDB = bugBountyReportRepository.findById(saved.getId()).orElseThrow(() -> new ResourceNotFoundException("Report not found"));
            // Set the bug bounty report for each collaborator
            for (CollaboratorDTO collaboratorDTO : reportPayload.getCollaboratorDTO()) {
                CollaboratorEntity collaboratorEntity = new CollaboratorEntity();
                collaboratorEntity.setCollaborationPercentage(collaboratorDTO.getCollaborationPercentage());
                collaboratorEntity.setHackerUsername(collaboratorDTO.getHackerUsername());
                collaboratorEntity.setBugBountyReport(reportFromDB);

//                collaborator.setBugBountyReport(report);
//                System.out.println(collaborator);
                collaboratorRepository.save(collaboratorEntity); // Save each collaborator to manage them
            }

            // Save the report and its collaborators
            bugBountyReportRepository.save(report);

            return BaseResponse.success("Bug bounty report submitted successfully");
        } else {
           throw new UnauthorizedException();
        }
    }


//    @PostMapping("/submit")
//    public ResponseEntity<?> submitBugBountyReport(@RequestBody ReportsEntity report, @RequestParam Long bugBountyProgramId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        programsRepository.findById(bugBountyProgramId).orElseThrow(()-> new ResourceNotFoundException("Program not found with id:" + bugBountyProgramId));
//        if (authentication != null && authentication.isAuthenticated()) {
//
//
//            String username = authentication.getName();
//            UserEntity user = userRepository.findByUsername(username).orElseThrow(()-> new UserNotFoundException("User with username " + username + " not found"));
//
//            if (user == null) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
//            }
//
//            // Set the user for the bug bounty report
//            report.setUser(user);
//
//
//            // Set the bug bounty program for the bug bounty report
//            BugBountyProgramEntity program = new BugBountyProgramEntity();
//            program.setId(bugBountyProgramId);
//            report.setBugBountyProgram(program);
//
//            System.out.println(program);
//
//            // Set the bug bounty report for each collaborator
//            for (CollaboratorEntity collaborator : report.getCollaborators()) {
//                collaborator.setBugBountyReport(report);
//                System.out.println(collaborator);
//                collaboratorRepository.save(collaborator);
//            }
//
//
//            System.out.println(report);
//
//            bugBountyReportRepository.save(report);
//            // Save the bug bounty report
//          //  bugBountyReportService.submitBugBountyReport(report);
//
//            return ResponseEntity.ok("Bug bounty report submitted successfully");
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
//        }
//    }



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
