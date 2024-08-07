package com.turingSecApp.turingSec.service.message;

import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.exception.custom.UnauthorizedException;
import com.turingSecApp.turingSec.model.entities.message.StringMessageInReport;
import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.model.repository.CompanyRepository;
import com.turingSecApp.turingSec.model.repository.program.ProgramRepository;
import com.turingSecApp.turingSec.model.repository.report.ReportRepository;
import com.turingSecApp.turingSec.model.repository.reportMessage.StringMessageInReportRepository;
import com.turingSecApp.turingSec.response.message.StringMessageInReportDTO;
import com.turingSecApp.turingSec.service.interfaces.IMessageInReportService;
import com.turingSecApp.turingSec.service.socket.ISocketEntityHelper;
import com.turingSecApp.turingSec.util.UtilService;
import com.turingSecApp.turingSec.util.mapper.StringMessageInReportMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageInReportService implements IMessageInReportService {

    private final ReportRepository reportRepository;
    private final ProgramRepository programRepository;
    private final CompanyRepository companyRepository;
    private final StringMessageInReportRepository stringMessageInReportRepository;
    private final UtilService utilService;
    private final ISocketEntityHelper socketEntityHelper;


    @Override
    public List<StringMessageInReportDTO> getMessagesByRoom(String room) {
        Report report = reportRepository.findByRoom(room)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with room: " + room));
                // Is it user or company if authorized
        Object authenticatedUser = utilService.getAuthenticatedBaseUser();
        log.info("User/Company info: " + authenticatedUser);

        // Validate the hacker/company and set Hacker
        checkUserOrCompanyReport(authenticatedUser, report.getId());

        List<StringMessageInReport> messages = stringMessageInReportRepository.findByReport_Id(report.getId());

        return messages.stream()
                .map(this::toStringMessageInReportDTO)
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
        checkUserOrCompanyReport(authenticatedUser, report.getId());

        return toStringMessageInReportDTO(message);
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

        return toStringMessageInReportDTO(message);
    }


    private StringMessageInReportDTO toStringMessageInReportDTO(StringMessageInReport savedMsg) {
        StringMessageInReportDTO dtoEagerFields = StringMessageInReportMapper.INSTANCE.toDTOEagerFields(savedMsg);

        // Fetch with query to avoid lazyInit exception
        Program programEntityForCompany = programRepository.findByReportsContains(savedMsg.getReport())
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with this report:" + savedMsg.getReport()));
        CompanyEntity companyEntityForProgram = companyRepository.findByBugBountyProgramsContains(programEntityForCompany)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with this program:" + programEntityForCompany));

        // Set programId to DTO
        dtoEagerFields.setProgramId(programEntityForCompany.getId());

        // Set companyId to DTO
        dtoEagerFields.setCompanyId(companyEntityForProgram.getId());

        return dtoEagerFields;
    }


    private void checkUserOrCompanyReport(Object authenticatedUser, Long reportId) {
        if (authenticatedUser instanceof UserEntity) {
            socketEntityHelper.checkUserReport(authenticatedUser, reportId);
        } else if (authenticatedUser instanceof CompanyEntity) {
            socketEntityHelper.checkCompanyReport(authenticatedUser, reportId);
        } else {
            throw new UnauthorizedException("User is neither Hacker nor Company!");
        }
    }

}
