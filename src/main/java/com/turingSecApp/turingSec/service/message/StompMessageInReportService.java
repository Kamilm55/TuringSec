package com.turingSecApp.turingSec.service.message;

import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.exception.custom.UnauthorizedException;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.model.entities.message.BaseMessageInReport;
import com.turingSecApp.turingSec.model.entities.message.StringMessageInReport;
import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.model.repository.CompanyRepository;
import com.turingSecApp.turingSec.model.repository.program.ProgramRepository;
import com.turingSecApp.turingSec.model.repository.report.ReportsRepository;
import com.turingSecApp.turingSec.model.repository.reportMessage.BaseMessageInReportRepository;
import com.turingSecApp.turingSec.model.repository.reportMessage.StringMessageInReportRepository;
import com.turingSecApp.turingSec.payload.message.StringMessageInReportPayload;
import com.turingSecApp.turingSec.response.message.StringMessageInReportDTO;
import com.turingSecApp.turingSec.service.interfaces.IStompMessageInReportService;
import com.turingSecApp.turingSec.service.socket.SocketEntityHelper;
import com.turingSecApp.turingSec.util.UtilService;
import com.turingSecApp.turingSec.util.mapper.StringMessageInReportMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class StompMessageInReportService implements IStompMessageInReportService {
    private final SimpMessagingTemplate messagingTemplate;
    private final ReportsRepository reportsRepository;
    private final SocketEntityHelper socketEntityHelper;
    private final ProgramRepository programRepository;
    private final CompanyRepository companyRepository;
    private final BaseMessageInReportRepository baseMessageInReportRepository;
    private final StringMessageInReportRepository stringMessageInReportRepository;
    private final UtilService utilService;
    @Override
    public void sendTextMessageToReportRoom(String room, StringMessageInReportPayload strMessageInReportPayload) {

        // todo: auth issues

        Report reportOfMessage = reportsRepository.findByRoom(room).orElseThrow(() -> new ResourceNotFoundException("Report not found with room: " + room));

//        // Is it user or company if authorized
//        Object authenticatedUser = getAuthenticatedUser();
//        log.info("User/Company info: " + authenticatedUser);
//
//        // Create message from payload
        StringMessageInReport strMessage = createStringMessageInReport(strMessageInReportPayload,null,reportOfMessage);

        // Save created strMessage obj
        StringMessageInReport savedMsg = stringMessageInReportRepository.save(strMessage);
        log.info(String.format("Data after save -> StringMessageInReport (entity): %s", savedMsg));

        // Convert to DTO with lazy fields' id
        StringMessageInReportDTO msgDTO = toStringMessageInReportDTO(savedMsg);
        log.info(String.format("Data after converting into DTO  -> StringMessageInReportDTO): %s", msgDTO));

        // Send message with socket
        messagingTemplate.convertAndSend(
                String.format("/topic/%s/messages", room), //  stompClient.subscribe('/topic/{room}/messages'...
                msgDTO
        );
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
    private Object getAuthenticatedUser() {
        try {
            return utilService.getAuthenticatedHacker();
        } catch (UserNotFoundException e) {
            // if exception occurs it is not Hacker
            log.warn("It is not Hacker entity!");
            return utilService.getAuthenticatedCompany();
        }
    }
    private StringMessageInReport createStringMessageInReport(StringMessageInReportPayload data,Object authenticatedUser,Report reportOfMessage) {
        StringMessageInReport strMessage = new StringMessageInReport();
        strMessage.setContent(data.getContent());
        strMessage.setEdited(false);
        strMessage.setCreatedAt(LocalDateTime.now());
        strMessage.setReplied(data.isReplied());

        // Set "BaseMessage" (replyTo) if it is not null
        if (data.getReplyToMessageId() != null) {
            Optional<BaseMessageInReport> repliedMessageInReport = baseMessageInReportRepository.findById(data.getReplyToMessageId());
            strMessage.setReplyTo(repliedMessageInReport.orElse(null));
        }

        // Set "Report"
        strMessage.setReport(reportOfMessage);

        // Set "isHacker"
        //todo: uncomment this
//        setHackerFlag(authenticatedUser, strMessage);

        log.info("Created StringMessageInReport object before save: " + strMessage);

        return strMessage;
    }
    //refactorThis
    private void checkUserOrCompanyReport(Object authenticatedUser, Long reportId) {
        if (authenticatedUser instanceof UserEntity) {
            socketEntityHelper.checkUserReport(authenticatedUser, reportId);
        } else if (authenticatedUser instanceof CompanyEntity) {
            socketEntityHelper.checkCompanyReport(authenticatedUser, reportId);
        } else {
            throw new UnauthorizedException("User is neither Hacker nor Company!");
        }
    }
    private void setHackerFlag(Object authenticatedUser, StringMessageInReport strMessage) {
        if (authenticatedUser instanceof UserEntity) {
            strMessage.setHacker(true);
        } else if (authenticatedUser instanceof CompanyEntity) {
            strMessage.setHacker(false);
        } else {
            throw new UnauthorizedException("User is neither Hacker nor Company!");
        }
    }
}
