package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.Request.ReportsByUserDTO;
import com.turingSecApp.turingSec.Request.ReportsByUserWithCompDTO;
import com.turingSecApp.turingSec.Request.UserDTO;
import com.turingSecApp.turingSec.dao.entities.*;
import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
import com.turingSecApp.turingSec.dao.repository.*;
import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.exception.custom.UnauthorizedException;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.payload.BugBountyReportPayload;
import com.turingSecApp.turingSec.payload.BugBountyReportUpdatePayload;
import com.turingSecApp.turingSec.payload.CollaboratorWithIdPayload;
import com.turingSecApp.turingSec.response.CollaboratorDTO;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.turingSecApp.turingSec.util.GlobalConstants.ROOT_LINK;

@Service
@RequiredArgsConstructor
public class BugBountyReportService {
    private final ProgramsRepository programsRepository;
    private final HackerRepository hackerRepository;
    private final ReportsRepository bugBountyReportRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CollaboratorRepository collaboratorRepository;

    public List<ReportsEntity> getAllBugBountyReports() {
        return bugBountyReportRepository.findAll();
    }

    public ReportsEntity getBugBountyReportById(Long id) {
        return bugBountyReportRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Report not found with id:"+id));
    }

    @Transactional
    public void submitBugBountyReport(ReportsEntity report) {
        ReportsEntity  reportFromDB = bugBountyReportRepository.findById(report.getId()).orElseThrow(()-> new ResourceNotFoundException("Report not found with id:" + report.getId()));

        // You can perform any necessary validation or processing here before saving the report
        bugBountyReportRepository.save(reportFromDB);
    }

    private String getUsernameFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        throw new RuntimeException("Unable to extract username from JWT token");
    }

    public ReportsEntity updateBugBountyReport(Long id, BugBountyReportUpdatePayload reportPayload) {
        ReportsEntity existingReport = bugBountyReportRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Report not found with id:"+id));

        // Fetch the BugBountyProgramEntity from the repository
//        BugBountyProgramEntity program = programsRepository.findById(bugBountyProgramId)
//                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id:" + bugBountyProgramId));

        // Update existingReport properties with values from updatedReport

        // Populate report entity
        existingReport.setAsset(reportPayload.getAsset());
        existingReport.setWeakness(reportPayload.getWeakness());
        existingReport.setSeverity(reportPayload.getSeverity());
        existingReport.setMethodName(reportPayload.getMethodName());
        existingReport.setProofOfConcept(reportPayload.getProofOfConcept());
        existingReport.setDiscoveryDetails(reportPayload.getDiscoveryDetails());
        existingReport.setLastActivity(reportPayload.getLastActivity());
        existingReport.setReportTitle(reportPayload.getReportTitle());
        existingReport.setRewardsStatus(reportPayload.getRewardsStatus());
        existingReport.setVulnerabilityUrl(reportPayload.getVulnerabilityUrl());


        // Set the user for the bug bounty report
        UserEntity userFromDB = userRepository.findById(reportPayload.getUserId()).orElseThrow(() -> new UserNotFoundException("User with id " + reportPayload.getUserId() + " not found"));
        existingReport.setUser(userFromDB);

        // Set the bug bounty program for the bug bounty report
//        report.setBugBountyProgram(program); // no need for update , but it should be when created

        ReportsEntity saved = bugBountyReportRepository.save(existingReport);

        ReportsEntity reportFromDB = bugBountyReportRepository.findById(saved.getId()).orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        // Set the bug bounty report for each collaborator
        for (CollaboratorWithIdPayload collaboratorDTO : reportPayload.getCollaborator()) {

            CollaboratorEntity collaboratorEntityFromDB = collaboratorRepository.findById(collaboratorDTO.getId()).orElseThrow(()-> new UserNotFoundException("Collaborator with id " + collaboratorDTO.getId() + " not found"));
            collaboratorEntityFromDB.setCollaborationPercentage(collaboratorDTO.getCollaborationPercentage());
            collaboratorEntityFromDB.setHackerUsername(collaboratorDTO.getHackerUsername());
            collaboratorEntityFromDB.setBugBountyReport(reportFromDB);

//                collaborator.setBugBountyReport(report);
//                System.out.println(collaborator);
            collaboratorRepository.save(collaboratorEntityFromDB); // Save each collaborator to manage them
        }

        System.out.println(existingReport);
        // Save the report and its collaborators
        return bugBountyReportRepository.save(existingReport);

    }

    public void deleteBugBountyReport(Long id) {
        // find exist or not if you need
        bugBountyReportRepository.deleteById(id);
    }


    public List<ReportsByUserWithCompDTO> getAllReportsByUser() {
        // Retrieve the username of the authenticated user
        String username = getUsernameFromToken();

        // Find the user by username
        UserEntity user = userRepository.findByUsername(username).orElseThrow(()-> new UserNotFoundException("User with username " + username + " not found"));

        // If user found, get all reports associated with that user
        if (user != null) {
            // Get all reports associated with the user
            List<ReportsEntity> userReports = bugBountyReportRepository.findByUser(user);

            // Group reports by user
            Map<UserDTO, List<ReportsEntity>> reportsByUser = userReports.stream()
                    .collect(Collectors.groupingBy(report -> new UserDTO(report.getUser().getId(), report.getUser().getUsername(), report.getUser().getEmail(),null,null,report.getUser().getHacker().getId()
                    )));

            // Create ReportsByUserDTO objects for each user and add them to the list
            List<ReportsByUserWithCompDTO> reportsByUsers = reportsByUser.entrySet().stream()
                    .map(entry -> {
                        UserDTO userDTO = entry.getKey();
                        List<ReportsEntity> reports = entry.getValue();
                        // Extract a single company name from bug bounty programs associated with the reports
                        String companyName = reports.stream()
                                .map(report -> report.getBugBountyProgram().getCompany().getCompany_name())
                                .findFirst()
                                .orElse(null);
                        // Create and return the ReportsByUserDTO object
                        return new ReportsByUserWithCompDTO(companyName, reports);
                    })
                    .collect(Collectors.toList());

            return reportsByUsers;
        } else {
            // If user not found, return an empty list or handle as needed
            return Collections.emptyList();
        }
    }


    public List<ReportsByUserDTO> getBugBountyReportsForCompanyPrograms(CompanyEntity company) {
        // Fetch the company entity along with its bug bounty programs within an active Hibernate session
        company = companyRepository.findById(company.getId()).orElse(null);
        if (company == null) {
            System.out.println("Company is not found");
            return Collections.emptyList();
        }

        // Access the bug bounty programs
        Set<BugBountyProgramEntity> bugBountyPrograms = company.getBugBountyPrograms();

        // Retrieve bug bounty reports submitted for the company's programs
        List<ReportsEntity> reports = bugBountyReportRepository.findByBugBountyProgramIn(bugBountyPrograms);

        // Group reports by user
        Map<UserDTO, List<ReportsEntity>> reportsByUser = reports.stream()
                .collect(Collectors.groupingBy(report -> new UserDTO(report.getUser().getId(), report.getUser().getUsername(), report.getUser().getEmail(),report.getUser().getFirst_name(),report.getUser().getLast_name(),report.getUser().getHacker().getId())));

        // Fetch image URL for each user
        Map<Long, String> userImgUrls = reportsByUser.keySet().stream()
                .collect(Collectors.toMap(UserDTO::getId, this::getUserImgUrl, (url1, url2) -> url1)); // Merge function to handle duplicate keys

        // Create ReportsByUserDTO objects for each user and add them to the list

//        List<ReportsByUserDTO> reportsByUsers = reportsByUser.entrySet().stream()
//                .map(entry -> {
//                    UserDTO userDTO = entry.getKey();
//                    List<ReportsEntity> userReports = entry.getValue();
//                    String imgUrl = userImgUrls.getOrDefault(userDTO.getId(), ""); // Get image URL for the user

//                    return new ReportsByUserDTO(userDTO, imgUrl, userReports);
//                })
//                .collect(Collectors.toList());
//
//        return reportsByUsers;

        return reportsByUser.entrySet().stream()
                .map(entry -> {
                    UserDTO userDTO = entry.getKey();
                    List<ReportsEntity> userReports = entry.getValue();
                   // String imgUrl = userImgUrls.getOrDefault(userDTO.getHackerId(), "");

                    ReportsByUserDTO reportsByUserDTO = new ReportsByUserDTO();
                    reportsByUserDTO.setUserId(userDTO.getId());
                    reportsByUserDTO.setUser(userDTO);
                    reportsByUserDTO.setReports(userReports);

                    HackerEntity hacker = hackerRepository.findById(userDTO.getHackerId()).orElseThrow(()->new UserNotFoundException("Hacker not found"));
                    reportsByUserDTO.setHas_hacker_profile_pic(hacker.isHas_profile_pic());

//                    if(hacker.isHas_profile_pic())
//                       reportsByUserDTO.setUserImgUrl(imgUrl);
                    return reportsByUserDTO;
                })
                .collect(Collectors.toList());
    }


    private String getUserImgUrl(UserDTO userDTO) {
    // https://turingsec-production-de02.up.railway.app
        return ROOT_LINK + "/api/background-image-for-hacker/download/" + userDTO.getHackerId();
    }

    public BaseResponse<?> submitBugBountyReport(BugBountyReportPayload reportPayload, Long bugBountyProgramId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Fetch the BugBountyProgramEntity from the repository
        BugBountyProgramEntity program = programsRepository.findById(bugBountyProgramId)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id:" + bugBountyProgramId));

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            userRepository.findByUsername(username)
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


    public void submitBugBountyReportForTest(BugBountyReportPayload reportPayload, Long bugBountyProgramId) {

        // Fetch the BugBountyProgramEntity from the repository
        BugBountyProgramEntity program = programsRepository.findById(bugBountyProgramId)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id:" + bugBountyProgramId));


//        String username = authentication.getName();
//        userRepository.findByUsername(username)
//                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));

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
    }

}
