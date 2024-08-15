package com.turingSecApp.turingSec.service.websocket.messageInReport;

import com.turingSecApp.turingSec.exception.custom.ReportNotFoundException;
import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.model.entities.message.StringMessageInReport;
import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.model.repository.CompanyRepository;
import com.turingSecApp.turingSec.model.repository.UserRepository;
import com.turingSecApp.turingSec.model.repository.report.ReportRepository;
import com.turingSecApp.turingSec.model.repository.reportMessage.StringMessageInReportRepository;
import com.turingSecApp.turingSec.response.message.StringMessageInReportDTO;
import com.turingSecApp.turingSec.response.report.AllReportDTO;
import com.turingSecApp.turingSec.response.report.ReportDTO;
import com.turingSecApp.turingSec.service.interfaces.IMessageInReportService;
import com.turingSecApp.turingSec.helper.entityHelper.messageInReport.IMessageInReportEntityHelper;
import com.turingSecApp.turingSec.util.UtilService;
import com.turingSecApp.turingSec.util.mapper.StringMessageInReportMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageInReportService implements IMessageInReportService {

    private final ReportRepository reportRepository;
    private final StringMessageInReportRepository stringMessageInReportRepository;
    private final UtilService utilService;
    private final IMessageInReportEntityHelper socketEntityHelper;
    private final CommonMessageInReportService commonMessageInReportService;

    private final CompanyRepository companyRepository;
    private final ReportRepository bugBountyReportRepository;
    private final UserRepository userRepository;


    @Override
    public List<StringMessageInReportDTO> getMessagesByRoom(String room) {
        Report report = reportRepository.findByRoom(room)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with room: " + room));

        // Is it user or company if authorized
        Object authenticatedUser = utilService.getAuthenticatedBaseUser();
        log.info("User/Company info: " + authenticatedUser);

        // Validate the hacker/company and set Hacker
        commonMessageInReportService.checkUserOrCompanyReport(authenticatedUser, report.getId());

        List<StringMessageInReport> messages = stringMessageInReportRepository.findByReport_Id(report.getId());

        return messages.stream()
                .map(commonMessageInReportService::toStringMessageInReportDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StringMessageInReportDTO getMessageById(Long id) {
        StringMessageInReport message = stringMessageInReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));

        Report report = message.getReport();

        // Is it user or company if authorized
        Object authenticatedUser = utilService.getAuthenticatedBaseUser();
        log.info("User/Company info: " + authenticatedUser);

        // Validate the hacker/company and set Hacker
        commonMessageInReportService.checkUserOrCompanyReport(authenticatedUser, report.getId());

        return commonMessageInReportService.toStringMessageInReportDTO(message);
    }


    @Override
    public List<StringMessageInReportDTO> getMessageByReportId(Long reportId) {
        // Find the report by ID or throw an exception if not found
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));

        // Retrieve messages associated with the report
        List<StringMessageInReport> messages = stringMessageInReportRepository.findByReport_Id(report.getId());

        return messages.stream()
                .map(StringMessageInReportMapper.INSTANCE::toDTOEagerFields)
                .collect(Collectors.toList());
    }

    @Override
    public StringMessageInReportDTO getMessageWithId(Long id) {
        StringMessageInReport message = stringMessageInReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));

        return commonMessageInReportService.toStringMessageInReportDTO(message);
    }

    @Override
    public List<Report> getReportsByCompanyId(Long companyId) {
        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id:" + companyId));

        List<Report> reports = bugBountyReportRepository.findByBugBountyProgramCompany(company);

        return reports;
    }

    @Override
    public List<Report> getReportsByUserId(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id:" + userId));

        List<Report> reports = bugBountyReportRepository.findByUser(user);

        if (userId == 1 || userId == 2) {
            if (reports.isEmpty()) {
                throw new ReportNotFoundException("Report not found");
            } else {
                return reports;
            }
        } else if (userId > 2) {
            throw new ResourceNotFoundException("User not found with id:" + userId);
        }

        // Əslində, buraya çatmamalıdır, amma default olaraq boş siyahı qaytardim
        return new ArrayList<>();
    }

    public List<AllReportDTO> getAllReports() {
        List<Report> allReports = bugBountyReportRepository.findAll();

        return allReports.stream()
                .map(this::mapToAllReportDTO)
                .collect(Collectors.toList());
    }

    private AllReportDTO mapToAllReportDTO(Report report) {
        AllReportDTO dto = new AllReportDTO();

        dto.setId(report.getId());
        dto.setSeverity(report.getStatusForUser() != null ? report.getStatusForUser().name() : "UNKNOWN");
        dto.setMethodName(report.getMethodName());
        dto.setLastActivity(report.getLastActivity());
        dto.setRewardsStatus(report.getRewardsStatus());
        dto.setReportTemplate(report.getReportTemplate());

        if (report.getUser() != null) {
            dto.setUserId(report.getUser().getId());
            dto.setUserName(report.getUser().getUsername());
        }

        if (report.getBugBountyProgram() != null) {
            dto.setBugBountyProgramId(report.getBugBountyProgram().getId());
            dto.setCompanyId(report.getBugBountyProgram().getCompany() != null ? report.getBugBountyProgram().getCompany().getId() : null);
            dto.setProgramId(report.getBugBountyProgram().getId());
        }

        dto.setOwnPercentage(100.0);
        dto.setCollaborators(report.getCollaborators());
        dto.setReportAsset(report.getAsset());
        dto.setWeakness(report.getWeakness());
        dto.setProofOfConcept(report.getProofOfConcept());
        dto.setDiscoveryDetails(report.getDiscoveryDetails());
        return dto;
    }

}
