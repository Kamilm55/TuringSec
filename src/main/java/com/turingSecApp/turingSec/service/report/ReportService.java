package com.turingSecApp.turingSec.service.report;

import com.turingSecApp.turingSec.exception.custom.ReportNotFoundException;
import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.model.entities.report.enums.REPORTSTATUSFORCOMPANY;
import com.turingSecApp.turingSec.model.entities.report.enums.REPORTSTATUSFORUSER;
import com.turingSecApp.turingSec.model.entities.user.BaseUser;
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
import com.turingSecApp.turingSec.service.EmailNotificationService;
import com.turingSecApp.turingSec.service.interfaces.INotificationService;
import com.turingSecApp.turingSec.service.interfaces.IReportService;
import com.turingSecApp.turingSec.service.user.CustomUserDetails;
import com.turingSecApp.turingSec.service.user.factory.UserFactory;
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
import static com.turingSecApp.turingSec.model.entities.report.enums.REPORTSTATUSFORUSER.SUBMITTED;


@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService implements IReportService {
    private final EmailNotificationService emailNotificationSevice;
    private final UserFactory userFactory;
    private final ReportRepository bugBountyReportRepository;
    private final ReportManualRepository reportManualRepository;
    private final ReportCVSSRepository reportCVSSRepository;
    private final UtilService utilService;
    private final IReportEntityHelper reportEntityHelper;
    private final ReportMediaService reportMediaService;
    private final GlobalConstants globalConstants;
    private  final ReportUtilService reportUtilService;

    private final INotificationService notificationService;

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
        setAuthenticatedUserToReport("191ded5d-148b-446d-8069-e8a8bd8c7ec7"/*mock uuid for "Username"*/, report);

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
        UserEntity authenticatedUser = (UserEntity) userFactory.getAuthenticatedBaseUser();

        // Fetch the BugBountyProgramEntity from the repository
        Program program = programRepository.findById(bugBountyProgramId)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id:" + bugBountyProgramId));

        // Create a new report entity -> for basic type fields or embeddable
        ReportManual report = reportEntityHelper.createReportManualFromPayload(reportPayload);

        // Set the user for the bug bounty report
        setAuthenticatedUserToReport(authenticatedUser.getId(), report);

        // Set the bug bounty program for the bug bounty report
        report.setBugBountyProgram(program);
        Report savedReport1 = reportManualRepository.save(report);

        // Set Attachments
        setAttachmentsToReportIfFilesExist(files, savedReport1);
        ///
        // Set child reference type fields with the relation
        ReportManual savedReport = (ReportManual) reportEntityHelper.setChildReferenceFieldsFromPayload(reportPayload,savedReport1);

        return savedReport;

    }

    private void setAttachmentsToReportIfFilesExist(List<MultipartFile> files, Report report) throws IOException {
        log.info("Files: " + files);

        // Call the service method to save the video
        if (files!=null){
            if(!files.isEmpty()){
                reportMediaService.saveFiles(files, report.getId());
            }
            else {
                log.info("Files are empty");
            }
        }
    }

    private void setAuthenticatedUserToReport(String authenticatedUserID, Report report) {
        UserEntity userFromDB = utilService.findUserById(authenticatedUserID);
        report.setUser(userFromDB);
    }

    @Override
    public ReportCVSS submitCVSSReport(List<MultipartFile> files, UserDetails userDetails,ReportCVSSPayload reportPayload, Long bugBountyProgramId) throws IOException {
        // Check the authenticated hacker
        UserEntity authenticatedUser = (UserEntity) userFactory.getAuthenticatedBaseUser();

        // Fetch the BugBountyProgramEntity from the repository
        Program program = programRepository.findById(bugBountyProgramId)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id:" + bugBountyProgramId));

        // Create a new report entity -> for basic type fields or embeddable
        ReportCVSS report = reportEntityHelper.createReportCVSSFromPayload(reportPayload);

        // Set the user for the bug bounty report
        setAuthenticatedUserToReport(authenticatedUser.getId(), report);

        // Set the bug bounty program for the bug bounty report
        report.setBugBountyProgram(program);
        Report savedReport1 = reportCVSSRepository.save(report);

        // Set Attachments
        setAttachmentsToReportIfFilesExist(files, savedReport1);
        ///
        // Set child reference type fields with the relation
        ReportCVSS savedReport = (ReportCVSS) reportEntityHelper.setChildReferenceFieldsFromPayload(reportPayload,savedReport1);

        return /*ReportMapper.INSTANCE.toDTO(savedReport)*/savedReport;
    }

    @Override // it is for admin finding by company with id -> not for current company
    public List<Report> getReportsByCompanyId(String companyId) {
        CompanyEntity company = utilService.findCompanyById(companyId);

        return bugBountyReportRepository.findByBugBountyProgramCompany(company);
    }
    @Override // // it is for admin finding by user with id -> not for current user
    public List<Report> getReportsByUserId(String userId) {
        UserEntity user =  utilService.findUserById(userId);

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
        CompanyEntity authenticatedCompany = (CompanyEntity) userFactory.getAuthenticatedBaseUser();
        reportUtilService.checkUserOrCompanyReport(authenticatedCompany, report.getId());

        report.setStatusForCompany(companyStatus);
        report.setStatusForUser(userStatus);

        UserEntity reportOwner = report.getUser();
        String message = String.format("Your report with ID %d has been %s by the company.", id, userStatus.name().toLowerCase());
        notificationService.saveNotification(reportOwner, message, "REPORT_STATUS_UPDATE");

        return bugBountyReportRepository.save(report);
    }
    @Override
    public List<ReportsByUserWithCompDTO> getReportsByUserWithStatus(REPORTSTATUSFORUSER status) {
        log.info("Provided status parameter: " + status);

        // Retrieve the authenticated user
        UserEntity user = (UserEntity) userFactory.getAuthenticatedBaseUser();

        // Retrieve all reports for the user if no status is provided or filtered reports for user
        List<Report> userReports = getReportsForStatus(status, user);

        // Group reports by user and create DTOs
        return createReportsByUserWithCompDTO(groupReportsByUser(userReports));
    }

    @Override
    public List<ReportsByUserDTO> getReportsByCompanyProgramWithStatus(REPORTSTATUSFORCOMPANY status) {
        log.info("Provided status parameter: " + status);

        // Retrieve the authenticated company
        CompanyEntity company = (CompanyEntity) userFactory.getAuthenticatedBaseUser();

        // Retrieve all reports for the user if no status is provided or filtered reports for user
        List<Report> userReports = getReportsForStatus(status, company);

        // Group reports by user, fetch image URLs, and create DTOs
        return createReportsByUserDTOList(groupReportsByUser(userReports), fetchUserImgUrls(groupReportsByUser(userReports)));
    }
  
    @Override // For admin -> it returns all report by any hackers or company
    public List<Report> getReportDateRange(LocalDate startDate, LocalDate endDate) {
        return filterReportsByDate(getAllReports(), startDate, endDate);
    }

    @Override
    public List<Report> getReportDateRangeCompanyId(LocalDate startDate, LocalDate endDate) {
        CompanyEntity company = (CompanyEntity) userFactory.getAuthenticatedBaseUser();

        return filterReportsByDate(getReportsForStatus(null,company), startDate, endDate);
    }

    @Override
    public List<Report> getReportDateRangeUserId(LocalDate startDate, LocalDate endDate) {
        UserEntity user = (UserEntity) userFactory.getAuthenticatedBaseUser();

        return filterReportsByDate(getReportsForStatus(null,user), startDate, endDate);
    }

    private List<Report> filterReportsByDate(List<Report> reports, LocalDate startDate, LocalDate endDate) {
        return reports.stream()
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

    @Override
    @Transactional
    public void deleteBugBountyReport(Long id) {
        // Ensure that the authenticated hacker can only update their own report
        Report report = bugBountyReportRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Report not found with id:" + id));

        //  Check report ownership
        BaseUser authenticatedBaseUser = userFactory.getAuthenticatedBaseUser();
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
    private void sendStatusChangeEmailForUser(Report report) {
        String to = report.getUser().getEmail();
        String subject = "The status of your report has changed";
        // List<Report> reports = report.getUser().getReports();
        String body;
        if (report.getStatusForUser().equals(SUBMITTED)) {
            body = String.format("Hello %s,\n" +
                            "\n" +
                            "Thank you for submitting your report to [Platform Name]. We have successfully received it and it is now in our system. The security team will review your report shortly.\n" +
                            "\n" +
                            "Stay tuned for updates!\n" +
                            "\n" +
                            "Best regards,\n" +
                            "TuringSec",
                    report.getUser().getUsername(),
                    report.getId());
        } else if (report.getStatusForUser().equals(UNDER_REVIEW)) {
            body = String.format(
                    "Hello %s,\n" +
                            "We wanted to let you know that your report, \"%d\", has moved to the Under Review stage. \n" +
                            "The security team is currently analyzing your findings.\n" +
                            "We'll keep you updated as they progress.\n" +
                            "Best regards,\n" +
                            "TuringSec",
                    report.getUser().getUsername(),
                    report.getId()
            );

        } else if (report.getStatusForUser().equals(ACCEPTED)) {
            //
            body = String.format(
                    " Hello %s,\n" +
                            "\n" +
                            "    Great news! The security team has accepted your report, \"%d\". Thank you for your valuable contribution to making [Platform Name] more secure.\n" +
                            "\n" +
                            "    You'll receive further details about your reward soon.\n" +
                            "\n" +
                            "    Best regards,\n" +
                            "   TuringSec",
                    report.getUser().getUsername(),
                    report.getId()
            );


        } else {
            body = String.format(
                    "  Hello %s,\n" +
                            "\n" +
                            "    Thank you for submitting your report, \"%s\". After careful review, the security team has determined that your report did not meet the criteria for acceptance.\n" +
                            "\n" +
                            "    We appreciate your effort and encourage you to continue contributing to our platform.\n" +
                            "\n" +
                            "    Best regards,\n" +
                            "    TuringSec",
                    report.getUser().getUsername(),
                    report.getId().toString()
            );

        }

        emailNotificationService.sendEmail(to, subject, body);
    }

    private void sendStatusChangeEmailForCompany(Report report) {
        String to = report.getUser().getEmail();
        String subject = "The status of your report has changed";
        List<Report> reports = report.getUser().getReports();
        String body;

        if (report.getStatusForUser().equals(SUBMITTED)) {
            body = String.format("Hello %s,\n" +
                            "\n" +
                            "    A new report titled \"%s\" has been submitted to [Platform Name]. The security team should begin reviewing the report shortly.\n" +
                            "\n" +
                            "    We'll keep you informed as we make progress.\n" +
                            "\n" +
                            "    Best regards,  \n" +
                            "    [Your Platform's Team]",
                    report.getUser().getUsername(),
                    report.getId().toString());

        } else if (report.getStatusForUser().equals(UNDER_REVIEW)) {

            body = String.format("   Hello %s,\n" +
                            "\n" +
                            "    The report titled \"%s\" has moved to the Under Review stage. The security team must currently analyzing the findings submitted by the hacker.\n" +
                            "\n" +
                            "    We'll provide updates as the review progresses.\n" +
                            "\n" +
                            "    Best regards,  \n" +
                            "    [Your Platform's Team]",
                    report.getUser().getUsername(),
                    report.getId().toString());

        } else if (report.getStatusForUser().equals(ACCEPTED)) {
            body = String.format("Hello  %s,\n" +
                            "\n" +
                            "    The security team has accepted the report titled \"%s\"  . The next step is to resolve the payment process.\n" +
                            "\n" +
                            "    Please review the details and let us know if any further steps are required.\n" +
                            "\n" +
                            "    Best regards,  \n" +
                            "    [Your Platform's Team]",
                    report.getUser().getUsername(),
                    report.getId().toString());
        } else {
            body = String.format(" Hello %s,\n" +
                            "\n" +
                            "    The security team has reviewed the report titled \"%s\" on your company's page and determined that it does not meet the criteria for acceptance.\n" +
                            "\n" +
                            "    You can view the details and rationale on your page. We appreciate your attention and will notify you of any future reports that may be relevant.\n" +
                            "\n" +
                            "    Best regards,  \n" +
                            "    [Your Platform's Team]",

                    report.getUser().getUsername(),
                    report.getId().toString());
        }

        emailNotificationSevice.sendEmail(to, subject, body);
    }

}
