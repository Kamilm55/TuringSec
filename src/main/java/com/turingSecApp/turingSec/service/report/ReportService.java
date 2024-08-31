package com.turingSecApp.turingSec.service.report;

import com.turingSecApp.turingSec.exception.custom.ReportNotFoundException;
import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.model.entities.report.enums.REPORTSTATUSFORCOMPANY;
import com.turingSecApp.turingSec.model.entities.report.enums.REPORTSTATUSFORUSER;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.entities.report.ReportCVSS;
import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.model.entities.report.ReportManual;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.model.repository.*;
import com.turingSecApp.turingSec.model.repository.program.ProgramRepository;
import com.turingSecApp.turingSec.model.repository.report.ReportCVSSRepository;
import com.turingSecApp.turingSec.model.repository.report.ReportManualRepository;
import com.turingSecApp.turingSec.model.repository.report.ReportRepository;
import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.file_upload.service.ReportMediaService;
import com.turingSecApp.turingSec.helper.entityHelper.report.IReportEntityHelper;
import com.turingSecApp.turingSec.payload.report.ReportCVSSPayload;
import com.turingSecApp.turingSec.payload.report.ReportDateRangeRequest;
import com.turingSecApp.turingSec.payload.report.ReportManualPayload;
import com.turingSecApp.turingSec.response.report.ReportsByUserDTO;
import com.turingSecApp.turingSec.response.report.ReportsByUserWithCompDTO;
import com.turingSecApp.turingSec.response.user.UserDTO;
import com.turingSecApp.turingSec.service.interfaces.IReportService;
import com.turingSecApp.turingSec.service.user.CustomUserDetails;
import com.turingSecApp.turingSec.util.GlobalConstants;
import com.turingSecApp.turingSec.util.ReportUtilService;
import com.turingSecApp.turingSec.util.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.turingSecApp.turingSec.model.entities.report.enums.REPORTSTATUSFORCOMPANY.*;
import static com.turingSecApp.turingSec.model.entities.report.enums.REPORTSTATUSFORUSER.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService implements IReportService {
    private final ReportRepository bugBountyReportRepository;
    private final ReportManualRepository reportManualRepository;
    private final ReportCVSSRepository reportCVSSRepository;
    private final UtilService utilService;
    private final IReportEntityHelper reportEntityHelper;
    private final ReportMediaService reportMediaService;
    private final GlobalConstants globalConstants;
    private  final ReportUtilService reportUtilService;

    private final ProgramRepository programRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    @Override
    public Report getBugBountyReportById(Long id) {
       return bugBountyReportRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Report not found with id:" + id));
    }
    @Override
    public ReportManual submitManualReportForTest(List<MultipartFile> files, ReportManualPayload reportPayload, Long bugBountyProgramId) throws IOException {
        // Fetch the BugBountyProgramEntity from the repository
        Program program = programRepository.findById(bugBountyProgramId)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id:" + bugBountyProgramId));

        // Create a new report entity -> for basic type fields or embeddable
        ReportManual report = reportEntityHelper.createReportManualFromPayload(reportPayload);

        // Set the user for the bug bounty report
        setAuthenticatedUserToReport(1L, report);

        // Set room explicitly
        report.setRoom("191ded5d-148b-446d-8069-e8a8bd8c7ec6");

        // Set the bug bounty program for the bug bounty report
        report.setBugBountyProgram(program);
        Report savedReport1 = reportManualRepository.save(report);

        ///
        // Set child reference type fields with the relation
        ReportManual savedReport = (ReportManual) reportEntityHelper.setChildReferenceFieldsFromPayload(reportPayload,savedReport1);

        return savedReport;

    }
    @Override
    public ReportManual submitManualReport(List<MultipartFile> files, UserDetails userDetails, ReportManualPayload reportPayload, Long bugBountyProgramId) throws IOException {
        // Check the authenticated hacker
        UserEntity authenticatedUser = utilService.getAuthenticatedHackerWithHTTP();

        // Fetch the BugBountyProgramEntity from the repository
        Program program = programRepository.findById(bugBountyProgramId)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id:" + bugBountyProgramId));

        // Create a new report entity -> for basic type fields or embeddable
        ReportManual report = reportEntityHelper.createReportManualFromPayload(reportPayload);

        // refactorThis: Set related entities (user and program)
        // Set the user for the bug bounty report
        setAuthenticatedUserToReport(authenticatedUser.getId(), report);

        // Set the bug bounty program for the bug bounty report
        report.setBugBountyProgram(program);
        Report savedReport1 = reportManualRepository.save(report);

        // Set Attachments
        setAttachmentsToReportIfFilesExist(files, userDetails, savedReport1);
        ///
        // Set child reference type fields with the relation
        ReportManual savedReport = (ReportManual) reportEntityHelper.setChildReferenceFieldsFromPayload(reportPayload,savedReport1);

        return savedReport;

    }

    private void setAttachmentsToReportIfFilesExist(List<MultipartFile> files, UserDetails userDetails, Report report) throws IOException {
        Long hackerId = utilService.validateHacker(userDetails);
        //todo: check report belongs to this user?

        // Call the service method to save the video
        if (files!=null){
            if(!files.isEmpty())
                reportMediaService.saveFiles(files, report.getId());
        }
    }

    private void setAuthenticatedUserToReport(Long authenticatedUserID, Report report) {
        UserEntity userFromDB = userRepository.findById(authenticatedUserID)
                .orElseThrow(() -> new UserNotFoundException("User with id " + authenticatedUserID + " not found"));
        report.setUser(userFromDB);
    }

    @Override
    public ReportCVSS submitCVSSReport(List<MultipartFile> files, UserDetails userDetails,ReportCVSSPayload reportPayload, Long bugBountyProgramId) throws IOException {
        // Check the authenticated hacker
        UserEntity authenticatedUser = utilService.getAuthenticatedHackerWithHTTP();

        // Fetch the BugBountyProgramEntity from the repository
        Program program = programRepository.findById(bugBountyProgramId)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id:" + bugBountyProgramId));

        // Create a new report entity -> for basic type fields or embeddable
        ReportCVSS report = reportEntityHelper.createReportCVSSFromPayload(reportPayload);

        // refactorThis: Set related entities (user and program)

        // Set the user for the bug bounty report
        setAuthenticatedUserToReport(authenticatedUser.getId(), report);

        // Set the bug bounty program for the bug bounty report
        report.setBugBountyProgram(program);
        Report savedReport1 = reportCVSSRepository.save(report);

        // Set Attachments
        setAttachmentsToReportIfFilesExist(files, userDetails, savedReport1);
        ///
        // Set child reference type fields with the relation
        ReportCVSS savedReport = (ReportCVSS) reportEntityHelper.setChildReferenceFieldsFromPayload(reportPayload,savedReport1);

        return /*ReportMapper.INSTANCE.toDTO(savedReport)*/savedReport;
    }

    @Override
    public List<Report> getReportsByCompanyId(Long companyId) {
        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id:" + companyId));

        return bugBountyReportRepository.findByBugBountyProgramCompany(company);
    }
    @Override
    public List<Report> getReportsByUserId(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id:" + userId));

        return bugBountyReportRepository.findByUser(user);
    }
    public List<Report> getAllReports() {
       return bugBountyReportRepository.findAll();
    }
    @Override
    public Report reviewReportByCompany(Long id) {
        return updateReportStatus(id, REVIEWED, UNDER_REVIEW);
    }

    @Override
    public Report acceptReportByCompany(Long id) {
        return updateReportStatus(id, ASSESSED, ACCEPTED);
    }

    @Override
    public Report rejectReportByCompany(Long id) {
        return updateReportStatus(id, ASSESSED, REJECTED);
    }

    private Report updateReportStatus(Long id, REPORTSTATUSFORCOMPANY companyStatus, REPORTSTATUSFORUSER userStatus) {
        Report report = getBugBountyReportById(id);

        // Check report belongs to this company
        CompanyEntity authenticatedCompany = utilService.getAuthenticatedCompanyWithHTTP();
        reportUtilService.checkUserOrCompanyReport(authenticatedCompany, report.getId());

        report.setStatusForCompany(companyStatus);
        report.setStatusForUser(userStatus);

        return bugBountyReportRepository.save(report);
    }
    @Override
    public List<ReportsByUserWithCompDTO> getReportsByUserWithStatus(REPORTSTATUSFORUSER status) {
        log.info("Provided status parameter: " + status);

        // Retrieve the authenticated user
        UserEntity user = utilService.getAuthenticatedHackerWithHTTP();

        // Retrieve all reports for the user if no status is provided or filtered reports for user
        List<Report> userReports = getReportsForStatus(status, user);

        // Group reports by user and create DTOs
        return createReportsByUserWithCompDTO(groupReportsByUser(userReports));
    }

    @Override
    public List<ReportsByUserDTO> getReportsByCompanyProgramWithStatus(REPORTSTATUSFORCOMPANY status) {
        log.info("Provided status parameter: " + status);

        // Retrieve the authenticated company
        CompanyEntity company = utilService.getAuthenticatedCompanyWithHTTP();

        // Retrieve all reports for the user if no status is provided or filtered reports for user
        List<Report> userReports = getReportsForStatus(status, company);

        // Group reports by user, fetch image URLs, and create DTOs
        return createReportsByUserDTOList(groupReportsByUser(userReports), fetchUserImgUrls(groupReportsByUser(userReports)));
    }

    @Override
    public List<Report> getReportByDateRange(LocalDate startDate,LocalDate endDate) {
        return getAllReports().stream()
                .filter(report -> !report.getCreatedAt().isBefore(startDate) && !report.getCreatedAt().isAfter(endDate))
                .collect(Collectors.toList());
    }

    private List<Report> getReportsForStatus(REPORTSTATUSFORUSER status, UserEntity user) {
        List<Report> userReports;
        if (status == null) {
            // Retrieve all reports for the user if no status is provided
            userReports = bugBountyReportRepository.findByUser(user);
        } else {
            // Get filtered reports for user
            userReports = getReports(status, user);
        }
        return userReports;
    }
    private List<Report> getReportsForStatus(REPORTSTATUSFORCOMPANY status, CompanyEntity company) {
        List<Report> userReports;
        if (status == null) {
            // Retrieve all reports for the company if no status is provided
            userReports = bugBountyReportRepository.findByBugBountyProgramCompany(company);
        } else {
            // Get filtered reports for company
            userReports = getReports(status, company);
        }
        return userReports;
    }

    // Get filtered reports for user
    private List<Report> getReports(REPORTSTATUSFORUSER status, UserEntity user) {
        // Validate the status and throw an exception for invalid values
        validateReportStatus(status);
        // Retrieve reports filtered by status
        return bugBountyReportRepository.findByUserAndStatusForUser(user, status);
    }

    // Get filtered reports for company
    private List<Report> getReports(REPORTSTATUSFORCOMPANY status, CompanyEntity company) {
        // Validate the status and throw an exception for invalid values
        validateReportStatus(status);
        // Retrieve reports filtered by status
        return bugBountyReportRepository.findByBugBountyProgramCompanyAndStatusForCompany(company, status);
    }

    // Validate user report status for acceptable values
    private void validateReportStatus(REPORTSTATUSFORUSER status) {
        Set<String> validStatusForUser = Stream.of(SUBMITTED, UNDER_REVIEW, ACCEPTED, REJECTED)
                .map(Enum::name)
                .collect(Collectors.toSet());

        if (!(validStatusForUser.contains(status.name().toUpperCase()))) {
            throw new IllegalArgumentException("Report status for user must be SUBMITTED, UNDER_REVIEW, ACCEPTED, or REJECTED.");
        }
    }

    // Validate company report status for acceptable values
    private void validateReportStatus(REPORTSTATUSFORCOMPANY status) {
        Set<String> validStatusForCompany = Stream.of(UNREVIEWED, REVIEWED, ASSESSED)
                .map(Enum::name)
                .collect(Collectors.toSet());

        if (!(validStatusForCompany.contains(status.name().toUpperCase()))) {
            throw new IllegalArgumentException("Report status for company must be REVIEWED, ASSESSED, or UNREVIEWED.");
        }
    }


//    private void checkEmpty(Long userId, List<Report> reports) {
//        if (reports.isEmpty()) {
//            throw new ReportNotFoundException("There is no report with user/company id: " + userId);
//        }
//    }
//    private void checkEmpty(List<Report> reports) {
//        if (reports.isEmpty()) {
//            throw new ReportNotFoundException("There is no report");
//        }
//    }
    @Override
    @Transactional
    public void deleteBugBountyReport(Long id) {
        // Ensure that the authenticated hacker can only update their own report
        Report report = bugBountyReportRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Report not found with id:" + id));

        //  Check report ownership
        Object authenticatedBaseUser = utilService.getAuthenticatedBaseUser();
        reportUtilService.checkUserOrCompanyReport(authenticatedBaseUser, report.getId());

        // Find related program then remove from list<ReportEntity>, then you can delete
        Program program = programRepository.findByReportsContains(report).orElseThrow(() -> new ResourceNotFoundException("Program not found with this report:" + report));
        program.removeReport(report.getId());

        Report updatedReport = reportEntityHelper.deleteReportChildEntities(report);

        bugBountyReportRepository.delete(updatedReport);
    }
    private Map<UserDTO, List<Report>> groupReportsByUser(List<Report> userReports) {
        return userReports.stream()
                .collect(Collectors.groupingBy(report ->
                        new UserDTO(report.getUser().getId(), report.getUser().getUsername(),
                                report.getUser().getEmail(), report.getUser().getFirst_name(), report.getUser().getLast_name(), report.getUser().getHacker().getId())));
    }

    private List<ReportsByUserWithCompDTO> createReportsByUserWithCompDTO(Map<UserDTO, List<Report>> reportsByUser) {
        return reportsByUser.entrySet().stream()
                .map(entry -> {
                    UserDTO userDTO = entry.getKey();
                    List<Report> reports = entry.getValue();
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



    private Map<Long, String> fetchUserImgUrls(Map<UserDTO, List<Report>> reportsByUser) {
        return reportsByUser.keySet().stream()
                .collect(Collectors.toMap(UserDTO::getHackerId, this::getUserImgUrl, (url1, url2) -> url1)); // Merge function to handle duplicate keys
    }


    private List<ReportsByUserDTO> createReportsByUserDTOList(Map<UserDTO, List<Report>> reportsByUser, Map<Long, String> userImgUrls) {
        return reportsByUser.entrySet().stream()
                .map(entry -> {
                    UserDTO userDTO = entry.getKey();
                    List<Report> userReports = entry.getValue();

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
        return globalConstants.ROOT_LINK + "/api/background-image-for-hacker/download/" + userDTO.getHackerId();
    }


}
