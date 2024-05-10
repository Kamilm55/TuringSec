package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.dao.entities.BugBountyProgramEntity;
import com.turingSecApp.turingSec.dao.entities.CollaboratorEntity;
import com.turingSecApp.turingSec.dao.entities.CompanyEntity;
import com.turingSecApp.turingSec.dao.entities.ReportEntity;
import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
import com.turingSecApp.turingSec.dao.repository.*;
import com.turingSecApp.turingSec.exception.custom.PermissionDeniedException;
import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.helper.entityHelper.IReportEntityHelper;
import com.turingSecApp.turingSec.helper.entityHelper.ReportEntityHelper;
import com.turingSecApp.turingSec.payload.report.BugBountyReportPayload;
import com.turingSecApp.turingSec.response.report.CollaboratorPayload;
import com.turingSecApp.turingSec.response.report.ReportDTO;
import com.turingSecApp.turingSec.response.report.ReportsByUserDTO;
import com.turingSecApp.turingSec.response.report.ReportsByUserWithCompDTO;
import com.turingSecApp.turingSec.response.user.UserDTO;
import com.turingSecApp.turingSec.service.interfaces.IBugBountyReportService;
import com.turingSecApp.turingSec.service.user.CustomUserDetails;
import com.turingSecApp.turingSec.util.UtilService;
import com.turingSecApp.turingSec.util.mapper.ReportMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class BugBountyReportService implements IBugBountyReportService {
    private final ReportsRepository bugBountyReportRepository;
    private final UtilService utilService;
    private final IReportEntityHelper reportEntityHelper;

    private final ProgramsRepository programsRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CollaboratorRepository collaboratorRepository;

    @Override
    public ReportDTO getBugBountyReportById(Long id) {
        ReportEntity report = bugBountyReportRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Report not found with id:" + id));
        return ReportMapper.INSTANCE.toDTO(report);
    }
    @Override
    public ReportDTO submitBugBountyReport(BugBountyReportPayload reportPayload, Long bugBountyProgramId) {
        // Check the authenticated hacker
        UserEntity authenticatedUser = utilService.getAuthenticatedHacker();

        // Fetch the BugBountyProgramEntity from the repository
        BugBountyProgramEntity program = programsRepository.findById(bugBountyProgramId)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id:" + bugBountyProgramId));

        // Create a new report entity
        ReportEntity report = createReportsEntityFromPayload(reportPayload);

        // Set the user for the bug bounty report
        UserEntity userFromDB = userRepository.findById(authenticatedUser.getId())
                .orElseThrow(() -> new UserNotFoundException("User with id " + authenticatedUser.getId() + " not found"));
        report.setUser(userFromDB);

        // Set the bug bounty program for the bug bounty report
        report.setBugBountyProgram(program);

        // Save the report and its collaborators
        ReportEntity savedReport = bugBountyReportRepository.save(report);
        saveCollaborators(reportPayload.getCollaboratorPayload(), savedReport);

        return ReportMapper.INSTANCE.toDTO(savedReport);
    }

    private void saveCollaborators(List<CollaboratorPayload> collaboratorDTOs, ReportEntity report) {
        for (var collaboratorDTO : collaboratorDTOs) {
            CollaboratorEntity collaboratorEntity = new CollaboratorEntity();
            userRepository.findByUsername(collaboratorDTO.getHackerUsername()).orElseThrow(() -> new UserNotFoundException("User with username '" + collaboratorDTO.getHackerUsername() + "' not found for collaborating"));
            collaboratorEntity.setCollaborationPercentage(collaboratorDTO.getCollaborationPercentage());
            collaboratorEntity.setHackerUsername(collaboratorDTO.getHackerUsername());
            collaboratorEntity.setBugBountyReport(report);

            collaboratorRepository.save(collaboratorEntity);

            report.addCollaborator(collaboratorEntity);
        }
        bugBountyReportRepository.save(report);
    }

    @Override
    public ReportDTO updateBugBountyReport(Long id, BugBountyReportPayload reportPayload) {
        // Retrieve existing report from the repository
        ReportEntity existingReport = bugBountyReportRepository.findById(id)
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

        // Clear previous collaborators, what you give in list i paste into  collaborators and update
        existingReport.setCollaborators(new ArrayList<>());

        // Update collaborators and save report
        saveCollaborators(reportPayload.getCollaboratorPayload() , existingReport);

        return ReportMapper.INSTANCE.toDTO(existingReport);
    }

    private void checkReportOwnership(ReportEntity report) {
        UserEntity authenticatedUser = utilService.getAuthenticatedHacker();

        if (!report.getUser().getId().equals(authenticatedUser.getId())) {
            throw new PermissionDeniedException();
        }
    }

    private void updateReportProperties(ReportEntity report, BugBountyReportPayload reportPayload) {
        setReportProperties(report, reportPayload);
    }

    private ReportEntity createReportsEntityFromPayload(BugBountyReportPayload reportPayload) {
        ReportEntity report = new ReportEntity();
        setReportProperties(report, reportPayload);
        return report;
    }

    private void setReportProperties(ReportEntity report, BugBountyReportPayload reportPayload) {
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
        report.setOwnPercentage(reportPayload.getOwnPercentage());
    }


    @Override
    @Transactional
    public void deleteBugBountyReport(Long id) {
        // Ensure that the authenticated hacker can only update their own report
        ReportEntity report = bugBountyReportRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Report not found with id:" + id));
        checkReportOwnership(report);

        // Find related program then remove from list<ReportEntity>, then you can delete
        BugBountyProgramEntity program = programsRepository.findByReportsContains(report).orElseThrow(() -> new ResourceNotFoundException("Report not found with report:" + report));
        program.removeReport(report.getId());

        ReportEntity updatedReport = reportEntityHelper.deleteReportChildEntities(report);

        bugBountyReportRepository.delete(updatedReport);
    }
    @Override
    public List<ReportsByUserWithCompDTO> getAllBugBountyReportsByUser() {
        // Retrieve the username of the authenticated user
        String username = getUsernameFromToken();

        // Find the user by username
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));

        // Get all reports associated with the user
        List<ReportEntity> userReports = bugBountyReportRepository.findByUser(user);

        // Group reports by user
        Map<UserDTO, List<ReportEntity>> reportsByUser = groupReportsByUser(userReports);

        // Create ReportsByUserDTO objects for each user and add them to the list
        return createReportsByUserWithCompDTO(reportsByUser);
    }

    private Map<UserDTO, List<ReportEntity>> groupReportsByUser(List<ReportEntity> userReports) {
        return userReports.stream()
                .collect(Collectors.groupingBy(report ->
                        new UserDTO(report.getUser().getId(), report.getUser().getUsername(),
                                report.getUser().getEmail(), report.getUser().getFirst_name(), report.getUser().getLast_name(), report.getUser().getHacker().getId())));
    }

    private List<ReportsByUserWithCompDTO> createReportsByUserWithCompDTO(Map<UserDTO, List<ReportEntity>> reportsByUser) {
        return reportsByUser.entrySet().stream()
                .map(entry -> {
                    UserDTO userDTO = entry.getKey();
                    List<ReportEntity> reports = entry.getValue();
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
        List<ReportEntity> reports = fetchReportsForPrograms(bugBountyPrograms);

        // Group reports by user
        Map<UserDTO, List<ReportEntity>> reportsByUser = groupReportsByUser(reports);

        // Fetch image URL for each user
        Map<Long, String> userImgUrls = fetchUserImgUrls(reportsByUser);

        return createReportsByUserDTOList(reportsByUser, userImgUrls);
    }

    private List<ReportEntity> fetchReportsForPrograms(Set<BugBountyProgramEntity> bugBountyPrograms) {
        return bugBountyReportRepository.findByBugBountyProgramIn(bugBountyPrograms);
    }

    private Map<Long, String> fetchUserImgUrls(Map<UserDTO, List<ReportEntity>> reportsByUser) {
        return reportsByUser.keySet().stream()
                .collect(Collectors.toMap(UserDTO::getHackerId, this::getUserImgUrl, (url1, url2) -> url1)); // Merge function to handle duplicate keys
    }


    private List<ReportsByUserDTO> createReportsByUserDTOList(Map<UserDTO, List<ReportEntity>> reportsByUser, Map<Long, String> userImgUrls) {
        return reportsByUser.entrySet().stream()
                .map(entry -> {
                    UserDTO userDTO = entry.getKey();
                    List<ReportEntity> userReports = entry.getValue();

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

        ReportEntity report = new ReportEntity();

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

        report.setOwnPercentage(reportPayload.getOwnPercentage());

        // Set the user for the bug bounty report
        UserEntity userFromDB = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
        report.setUser(userFromDB);

        // Set the bug bounty program for the bug bounty report
        report.setBugBountyProgram(program);

        ReportEntity saved = bugBountyReportRepository.save(report);

        ReportEntity reportFromDB = bugBountyReportRepository.findById(saved.getId()).orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        // Set the bug bounty report for each collaborator
        for (var collaboratorDTO : reportPayload.getCollaboratorPayload()) {
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
