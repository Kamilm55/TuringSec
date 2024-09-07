package com.turingSecApp.turingSec.service.websocket.messageInReport;

import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.exception.custom.UnauthorizedException;
import com.turingSecApp.turingSec.helper.entityHelper.messageInReport.IMessageInReportEntityHelper;
import com.turingSecApp.turingSec.model.entities.message.BaseMessageInReport;
import com.turingSecApp.turingSec.model.entities.message.StringMessageInReport;
import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.model.repository.CompanyRepository;
import com.turingSecApp.turingSec.model.repository.UserRepository;
import com.turingSecApp.turingSec.model.repository.program.ProgramRepository;
import com.turingSecApp.turingSec.model.repository.reportMessage.BaseMessageInReportRepository;
import com.turingSecApp.turingSec.payload.message.StringMessageInReportPayload;
import com.turingSecApp.turingSec.response.message.StringMessageInReportDTO;
import com.turingSecApp.turingSec.service.interfaces.ICommonMessageInReportService;
import com.turingSecApp.turingSec.util.UtilService;
import com.turingSecApp.turingSec.util.mapper.StringMessageInReportMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommonMessageInReportService implements ICommonMessageInReportService {
    private final IMessageInReportEntityHelper messageInReportEntityHelper;
    private final BaseMessageInReportRepository baseMessageInReportRepository;
    private final CompanyRepository companyRepository;
    private final ProgramRepository programRepository;
    private final UserRepository userRepository;

    @Override
    public void checkUserOrCompanyReport(Object authenticatedUser, Long reportId) {
        if (authenticatedUser instanceof UserEntity) {
            log.info("It is User Entity");
            messageInReportEntityHelper.checkUserReport(authenticatedUser, reportId);
        } else if (authenticatedUser instanceof CompanyEntity) {
            log.info("It is Company Entity");
            messageInReportEntityHelper.checkCompanyReport(authenticatedUser, reportId);
        } else {
            throw new UnauthorizedException("User is neither Hacker nor Company!");
        }
    }

    @Override
    public StringMessageInReport createStringMessageInReport(StringMessageInReportPayload data, Object authenticatedUser, Report reportOfMessage) {
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
        setHackerFlag(authenticatedUser, strMessage);

        log.info("Created StringMessageInReport object before save: " + strMessage);

        return strMessage;
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

    @Override
    public StringMessageInReportDTO toStringMessageInReportDTO(StringMessageInReport savedMsg) {
        StringMessageInReportDTO dtoEagerFields = StringMessageInReportMapper.INSTANCE.toDTOEagerFields(savedMsg);

        // Fetch with query to avoid lazyInit exception
        UserEntity userOfReport = userRepository.findByReportsContains(savedMsg.getReport())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with this report:" + savedMsg.getReport()));;
        Program programEntityForCompany = programRepository.findByReportsContains(savedMsg.getReport())
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with this report:" + savedMsg.getReport()));
        CompanyEntity companyEntityForProgram = companyRepository.findByBugBountyProgramsContains(programEntityForCompany)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with this program:" + programEntityForCompany));

        // Set lazy entity id to DTO explicitly
        dtoEagerFields.setUserId(userOfReport.getId());
        dtoEagerFields.setProgramId(programEntityForCompany.getId());
        dtoEagerFields.setCompanyId(companyEntityForProgram.getId());

        return dtoEagerFields;
    }
}
