package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.response.ReportsByUserDTO;
import com.turingSecApp.turingSec.response.ReportsByUserWithCompDTO;
import com.turingSecApp.turingSec.response.UserDTO;
import com.turingSecApp.turingSec.dao.entities.*;
import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
import com.turingSecApp.turingSec.dao.repository.*;
import com.turingSecApp.turingSec.exception.custom.PermissionDeniedException;
import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.payload.BugBountyReportPayload;
import com.turingSecApp.turingSec.payload.BugBountyReportUpdatePayload;
import com.turingSecApp.turingSec.payload.CollaboratorWithIdPayload;
import com.turingSecApp.turingSec.service.interfaces.IBugBountyReportService;
import com.turingSecApp.turingSec.service.user.CustomUserDetails;
import com.turingSecApp.turingSec.util.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.turingSecApp.turingSec.util.GlobalConstants.ROOT_LINK;

@Service
@RequiredArgsConstructor
@Slf4j
public class BugBountyReportService implements IBugBountyReportService {
    private final ReportsRepository bugBountyReportRepository;
    private final UtilService utilService;

    private final ProgramsRepository programsRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CollaboratorRepository collaboratorRepository;

    @Override
    public ReportsEntity getBugBountyReportById(Long id) {
        return bugBountyReportRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Report not found with id:"+id));
    }
    @Override
    public void submitBugBountyReport(BugBountyReportPayload reportPayload, Long bugBountyProgramId) {
        // Check the authenticated hacker
        UserEntity authenticatedUser = utilService.getAuthenticatedHacker();

        // Fetch the BugBountyProgramEntity from the repository
        BugBountyProgramEntity program = programsRepository.findById(bugBountyProgramId)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id:" + bugBountyProgramId));

        // Create a new report entity
        ReportsEntity report = createReportsEntityFromPayload(reportPayload);

        // Set the user for the bug bounty report
        UserEntity userFromDB = userRepository.findById(authenticatedUser.getId())
                .orElseThrow(() -> new UserNotFoundException("User with id " + authenticatedUser.getId() + " not found"));
        report.setUser(userFromDB);

        // Set the bug bounty program for the bug bounty report
        report.setBugBountyProgram(program);

        // Save the report and its collaborators
        ReportsEntity savedReport = bugBountyReportRepository.save(report);
        saveCollaborators(reportPayload.getCollaboratorDTO(), savedReport);
    }

    private ReportsEntity createReportsEntityFromPayload(BugBountyReportPayload reportPayload) {
        ReportsEntity report = new ReportsEntity();
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
        return report;
    }

    private void saveCollaborators(List<CollaboratorWithIdPayload> collaboratorDTOs, ReportsEntity report) {
        for (CollaboratorWithIdPayload collaboratorDTO : collaboratorDTOs) {
            CollaboratorEntity collaboratorEntity = new CollaboratorEntity();
            collaboratorEntity.setCollaborationPercentage(collaboratorDTO.getCollaborationPercentage());
            collaboratorEntity.setHackerUsername(collaboratorDTO.getHackerUsername());
            collaboratorEntity.setBugBountyReport(report);
            collaboratorRepository.save(collaboratorEntity);
        }
    }


    @Override
    public ReportsEntity updateBugBountyReport(Long id, BugBountyReportUpdatePayload reportPayload) {
        // Retrieve existing report from the repository
        ReportsEntity existingReport = bugBountyReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id:" + id));

        // Ensure that the authenticated hacker can only update their own report
        UserEntity authenticatedUser = utilService.getAuthenticatedHacker();
        checkReportOwnership(existingReport);

        // Update existing report properties with values from the update payload
        updateReportProperties(existingReport, reportPayload);

        // Set the user for the bug bounty report
        UserEntity userFromDB = userRepository.findById(authenticatedUser.getId())
                .orElseThrow(() -> new UserNotFoundException("User with id " +authenticatedUser.getId() + " not found"));
        existingReport.setUser(userFromDB);

        // Update collaborators
        List<CollaboratorEntity> updatedCollaborators = updateCollaborators(existingReport, reportPayload.getCollaborator());

        existingReport.setCollaborators(updatedCollaborators);
        bugBountyReportRepository.save(existingReport);

        // Save the updated report and return it
        return bugBountyReportRepository.save(existingReport);
    }

    private void checkReportOwnership(ReportsEntity report) {
        UserEntity authenticatedUser = utilService.getAuthenticatedHacker();

        if (!report.getUser().getId().equals(authenticatedUser.getId())) {
            throw new PermissionDeniedException();
        }
    }

    private void updateReportProperties(ReportsEntity report, BugBountyReportUpdatePayload reportPayload) {
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
    }

    private List<CollaboratorEntity> updateCollaborators(ReportsEntity report, List<CollaboratorWithIdPayload> collaboratorPayloads) {
        List<CollaboratorEntity> collaboratorEntities = new ArrayList<>();
        for (CollaboratorWithIdPayload collaboratorDTO : collaboratorPayloads) {
            CollaboratorEntity collaboratorEntityFromDB = collaboratorRepository.findById(collaboratorDTO.getId())
                    .orElseThrow(() -> new UserNotFoundException("Collaborator with id " + collaboratorDTO.getId() + " not found"));
            collaboratorEntityFromDB.setCollaborationPercentage(collaboratorDTO.getCollaborationPercentage());
            collaboratorEntityFromDB.setHackerUsername(collaboratorDTO.getHackerUsername());
            collaboratorEntityFromDB.setBugBountyReport(report);
            collaboratorEntities.add(collaboratorEntityFromDB);
//            collaboratorRepository.save(collaboratorEntityFromDB);
        }
        return collaboratorEntities;
    }


    @Override
    public void deleteBugBountyReport(Long id) {
        // Ensure that the authenticated hacker can only update their own report
        checkReportOwnership(bugBountyReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id:" + id)));

        // find exist or not if you need
        bugBountyReportRepository.deleteById(id);
    }
    @Override
    public List<ReportsByUserWithCompDTO> getAllBugBountyReportsByUser() {
        // Retrieve the username of the authenticated user
        String username = getUsernameFromToken();

        // Find the user by username
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));

        // Get all reports associated with the user
        List<ReportsEntity> userReports = bugBountyReportRepository.findByUser(user);

        // Group reports by user
        Map<UserDTO, List<ReportsEntity>> reportsByUser = groupReportsByUser(userReports);

        // Create ReportsByUserDTO objects for each user and add them to the list
        return createReportsByUserWithCompDTO(reportsByUser);
    }

    private Map<UserDTO, List<ReportsEntity>> groupReportsByUser(List<ReportsEntity> userReports) {
        return userReports.stream()
                .collect(Collectors.groupingBy(report ->
                        new UserDTO(report.getUser().getId(), report.getUser().getUsername(),
                                report.getUser().getEmail(), report.getUser().getFirst_name(), report.getUser().getLast_name(), report.getUser().getHacker().getId())));
    }

    private List<ReportsByUserWithCompDTO> createReportsByUserWithCompDTO(Map<UserDTO, List<ReportsEntity>> reportsByUser) {
        return reportsByUser.entrySet().stream()
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
    }

   //
   @Override
   public List<ReportsByUserDTO> getBugBountyReportsForCompanyPrograms() {
       // Retrieve the authenticated user details
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

       // Extract the company from the authenticated user details
       CompanyEntity company = extractCompanyFromUserDetails(userDetails);

       // Retrieve bug bounty reports submitted for the company's programs
       return getReportsForCompanyPrograms(company);
   }

    private CompanyEntity extractCompanyFromUserDetails(CustomUserDetails userDetails) {
        Object user = userDetails.getUser();
        if (!(user instanceof CompanyEntity)) {
            throw new IllegalStateException("Authenticated user is not a company");
        }
        return (CompanyEntity) user;
    }

    private List<ReportsByUserDTO> getReportsForCompanyPrograms(CompanyEntity company) {
        // Fetch the company entity along with its bug bounty programs within an active Hibernate session
        company = companyRepository.findById(company.getId()).orElse(null);
        if (company == null) {
            log.info("Company is not found , in getReportsForCompanyPrograms method ");
            return Collections.emptyList();
        }

        // Retrieve bug bounty programs associated with the company
        Set<BugBountyProgramEntity> bugBountyPrograms = company.getBugBountyPrograms();

        // Retrieve bug bounty reports submitted for the company's programs
        List<ReportsEntity> reports = fetchReportsForPrograms(bugBountyPrograms);

        // Group reports by user
        Map<UserDTO, List<ReportsEntity>> reportsByUser = groupReportsByUser(reports);

        // Fetch image URL for each user
        Map<Long, String> userImgUrls = fetchUserImgUrls(reportsByUser);

        return createReportsByUserDTOList(reportsByUser, userImgUrls);
    }

    private List<ReportsEntity> fetchReportsForPrograms(Set<BugBountyProgramEntity> bugBountyPrograms) {
        return bugBountyReportRepository.findByBugBountyProgramIn(bugBountyPrograms);
    }

    private Map<Long, String> fetchUserImgUrls(Map<UserDTO, List<ReportsEntity>> reportsByUser) {
        return reportsByUser.keySet().stream()
                .collect(Collectors.toMap(UserDTO::getHackerId, this::getUserImgUrl, (url1, url2) -> url1)); // Merge function to handle duplicate keys
    }


    private List<ReportsByUserDTO> createReportsByUserDTOList(Map<UserDTO, List<ReportsEntity>> reportsByUser, Map<Long, String> userImgUrls) {
        return reportsByUser.entrySet().stream()
                .map(entry -> {
                    UserDTO userDTO = entry.getKey();
                    List<ReportsEntity> userReports = entry.getValue();

                    ReportsByUserDTO reportsByUserDTO = new ReportsByUserDTO();
                    reportsByUserDTO.setUserId(userDTO.getId());
                    reportsByUserDTO.setUser(userDTO);
                    reportsByUserDTO.setReports(userReports);

                    reportsByUserDTO.setHas_hacker_profile_pic(userImgUrls.containsKey(userDTO.getHackerId()));
                    // reportsByUserDTO.setUserImgUrl(userImgUrls.getOrDefault(userDTO.getHackerId(), ""));
                    return reportsByUserDTO;
                })
                .collect(Collectors.toList());
    }


    private String getUserImgUrl(UserDTO userDTO) {
    // https://turingsec-production-de02.up.railway.app
        return ROOT_LINK + "/api/background-image-for-hacker/download/" + userDTO.getHackerId();
    }

    private String getUsernameFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        throw new RuntimeException("Unable to extract username from JWT token");
    }


    // For CommandLineRunner \\ TEST //
    public void submitBugBountyReportForTest(BugBountyReportPayload reportPayload, Long bugBountyProgramId ,Long userId) {

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
        UserEntity userFromDB = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
        report.setUser(userFromDB);

        // Set the bug bounty program for the bug bounty report
        report.setBugBountyProgram(program);

        ReportsEntity saved = bugBountyReportRepository.save(report);

        ReportsEntity reportFromDB = bugBountyReportRepository.findById(saved.getId()).orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        // Set the bug bounty report for each collaborator
        for (var collaboratorDTO : reportPayload.getCollaboratorDTO()) {
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
