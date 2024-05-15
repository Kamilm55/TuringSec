package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.dao.entities.BugBountyProgramEntity;
import com.turingSecApp.turingSec.dao.entities.CompanyEntity;
import com.turingSecApp.turingSec.dao.entities.report.ReportCVSS;
import com.turingSecApp.turingSec.dao.entities.report.ReportEntity;
import com.turingSecApp.turingSec.dao.entities.report.ReportManual;
import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
import com.turingSecApp.turingSec.dao.repository.*;
import com.turingSecApp.turingSec.exception.custom.PermissionDeniedException;
import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.helper.entityHelper.IReportEntityHelper;
import com.turingSecApp.turingSec.payload.report.BugBountyReportPayload;
import com.turingSecApp.turingSec.payload.report.ReportCVSSPayload;
import com.turingSecApp.turingSec.payload.report.ReportManualPayload;
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
    private final ReportManualRepository reportManualRepository;
    private final ReportCVSSRepository reportCVSSRepository;
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
    public /*ReportManualDTO*/ReportManual submitManualReport(ReportManualPayload reportPayload, Long bugBountyProgramId) {
        // Check the authenticated hacker
        UserEntity authenticatedUser = utilService.getAuthenticatedHacker();

        // Fetch the BugBountyProgramEntity from the repository
        BugBountyProgramEntity program = programsRepository.findById(bugBountyProgramId)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id:" + bugBountyProgramId));

        // Create a new report entity -> for basic type fields or embeddable
        ReportManual report = reportEntityHelper.createReportManualFromPayload(reportPayload);

        // refactorThis: Set related entities (user and program)

        // Set the user for the bug bounty report
        setAuthenticatedUserToReport(authenticatedUser.getId(), report);

        // Set the bug bounty program for the bug bounty report
        report.setBugBountyProgram(program);
        ReportEntity savedReport1 = reportManualRepository.save(report);

        ///
        // Set child reference type fields with the relation
        ReportManual savedReport = (ReportManual) reportEntityHelper.setChildReferenceFieldsFromPayload(reportPayload,savedReport1);

        return /*ReportMapper.INSTANCE.toDTO(savedReport)*/savedReport;

    }
//    @Override
//    public ReportDTO submitBugBountyReport(BugBountyReportPayload reportPayload, Long bugBountyProgramId) {
//        // Check the authenticated hacker
//        UserEntity authenticatedUser = utilService.getAuthenticatedHacker();
//
//        // Fetch the BugBountyProgramEntity from the repository
//        BugBountyProgramEntity program = programsRepository.findById(bugBountyProgramId)
//                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id:" + bugBountyProgramId));
//
//        // Create a new report entity -> for basic type fields or embeddable
//        ReportEntity report = reportEntityHelper.createReportsEntityFromPayload(reportPayload);
//
//        // refactorThis: Set related entities (user and program)
//
//        // Set the user for the bug bounty report
//        setAuthenticatedUserToReport(authenticatedUser.getId(), report);
//
//        // Set the bug bounty program for the bug bounty report
//        report.setBugBountyProgram(program);
//        ReportEntity savedReport1 = bugBountyReportRepository.save(report);
//
//        ///
//        // Set child reference type fields with the relation
//        ReportEntity savedReport = reportEntityHelper.setChildReferenceFieldsFromPayload(reportPayload,savedReport1);
//
//        return ReportMapper.INSTANCE.toDTO(savedReport);
//    }

    private void setAuthenticatedUserToReport(Long authenticatedUser, ReportEntity report) {
        UserEntity userFromDB = userRepository.findById(authenticatedUser)
                .orElseThrow(() -> new UserNotFoundException("User with id " + authenticatedUser + " not found"));
        report.setUser(userFromDB);
    }
    @Override
    public ReportManual updateManualReport(Long id, ReportManualPayload reportPayload) {
        // Retrieve existing report from the repository
        ReportManual existingReport = reportManualRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id:" + id));

        // Ensure that the authenticated hacker can only update their own report
        UserEntity authenticatedUser = utilService.getAuthenticatedHacker();
        checkReportOwnership(existingReport);

        // Update existing report properties with values from the update payload
        updateReportProperties(existingReport, reportPayload);


        // Set the user for the bug bounty report
        setAuthenticatedUserToReport(authenticatedUser.getId(), existingReport);

        // todo: updating related bidirectional fields like submit method to combine post and put methods
        // Clear previous collaborators, what you give in list i paste into  collaborators and update
        existingReport.setCollaborators(new ArrayList<>());

        // Update collaborators and save report
        reportEntityHelper.saveCollaborators(reportPayload.getCollaboratorPayload() , existingReport);

        return /*ReportMapper.INSTANCE.toDTO(existingReport)*/existingReport;
    }

    @Override
    public ReportCVSS submitCVSSReport(ReportCVSSPayload reportPayload, Long bugBountyProgramId) {
        // Check the authenticated hacker
        UserEntity authenticatedUser = utilService.getAuthenticatedHacker();

        // Fetch the BugBountyProgramEntity from the repository
        BugBountyProgramEntity program = programsRepository.findById(bugBountyProgramId)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id:" + bugBountyProgramId));

        // Create a new report entity -> for basic type fields or embeddable
        ReportCVSS report = reportEntityHelper.createReportCVSSFromPayload(reportPayload);

        // refactorThis: Set related entities (user and program)

        // Set the user for the bug bounty report
        setAuthenticatedUserToReport(authenticatedUser.getId(), report);

        // Set the bug bounty program for the bug bounty report
        report.setBugBountyProgram(program);
        ReportEntity savedReport1 = reportCVSSRepository.save(report);

        ///
        // Set child reference type fields with the relation
        ReportCVSS savedReport = (ReportCVSS) reportEntityHelper.setChildReferenceFieldsFromPayload(reportPayload,savedReport1);

        return /*ReportMapper.INSTANCE.toDTO(savedReport)*/savedReport;


    }

    @Override
    public ReportCVSS updateCVSSReport(Long id, ReportCVSSPayload bugBountyReportUpdatePayload) {
        // Retrieve existing report from the repository
        ReportCVSS existingReport = reportCVSSRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id:" + id));

        // Ensure that the authenticated hacker can only update their own report
        UserEntity authenticatedUser = utilService.getAuthenticatedHacker();
        checkReportOwnership(existingReport);

        // Update existing report properties with values from the update payload
        updateReportProperties(existingReport, bugBountyReportUpdatePayload);


        // Set the user for the bug bounty report
        setAuthenticatedUserToReport(authenticatedUser.getId(), existingReport);

        // todo: updating related bidirectional fields like submit method to combine post and put methods
        // Clear previous collaborators, what you give in list i paste into  collaborators and update
        existingReport.setCollaborators(new ArrayList<>());

        // Update collaborators and save report
        reportEntityHelper.saveCollaborators(bugBountyReportUpdatePayload.getCollaboratorPayload() , existingReport);

        return /*ReportMapper.INSTANCE.toDTO(existingReport)*/existingReport;

    }

    private void checkReportOwnership(ReportEntity report) {
        UserEntity authenticatedUser = utilService.getAuthenticatedHacker();

        if (!report.getUser().getId().equals(authenticatedUser.getId())) {
            throw new PermissionDeniedException();
        }
    }

    private void updateReportProperties(ReportManual report, ReportManualPayload reportPayload) {
        reportEntityHelper.setCommonReportProperties(report, reportPayload);
        report.setSeverity(reportPayload.getSeverity());
    }
    private void updateReportProperties(ReportCVSS report, ReportCVSSPayload reportPayload) {
        reportEntityHelper.setCommonReportProperties(report, reportPayload);
        reportEntityHelper.setCVSSFields(report,reportPayload);
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

        // Create a new report entity
        ReportEntity report = reportEntityHelper.createReportsEntityFromPayload(reportPayload);

        // Set the user for the bug bounty report
        setAuthenticatedUserToReport(userId, report);

        // Set the bug bounty program for the bug bounty report
        report.setBugBountyProgram(program);

        // Set child reference type fields

        // Save the report and its collaborators
        ReportEntity savedReport = bugBountyReportRepository.save(report);
        reportEntityHelper.saveCollaborators(reportPayload.getCollaboratorPayload(), savedReport);


    }
}
