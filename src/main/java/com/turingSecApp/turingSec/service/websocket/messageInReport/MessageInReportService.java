package com.turingSecApp.turingSec.service.websocket.messageInReport;

import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.model.entities.message.StringMessageInReport;
import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.model.repository.report.ReportRepository;
import com.turingSecApp.turingSec.model.repository.reportMessage.StringMessageInReportRepository;
import com.turingSecApp.turingSec.response.message.StringMessageInReportDTO;
import com.turingSecApp.turingSec.service.interfaces.IMessageInReportService;
import com.turingSecApp.turingSec.helper.entityHelper.messageInReport.IMessageInReportEntityHelper;
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
    private final StringMessageInReportRepository stringMessageInReportRepository;
    private final UtilService utilService;
    private final IMessageInReportEntityHelper socketEntityHelper;
    private final CommonMessageInReportService commonMessageInReportService;


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

}
