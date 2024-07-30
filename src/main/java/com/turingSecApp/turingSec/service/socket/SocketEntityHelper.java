package com.turingSecApp.turingSec.service.socket;

import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.exception.custom.UserMustBeSameWithReportUserException;
import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.model.repository.report.ReportsRepository;
import com.turingSecApp.turingSec.service.socket.exceptionHandling.ISocketEntityHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SocketEntityHelper implements ISocketEntityHelper {
    private final ReportsRepository reportsRepository;

    // Learn: Issue Explanation
    //  1.Proxy-Based Transaction Management:
    //  Spring uses proxies to manage transactions. These proxies intercept method calls to handle transactions according to the @Transactional annotations.
    //  If a transactional method is called from within the same class (self-invocation), the Spring proxy does not intercept the call. This means that transaction management (and by extension, session management) does not apply, leading to issues like LazyInitializationException.
    //  2.Self-Invocation Problem:
    //  When a method annotated with @Transactional is called from another method within the same class, the call bypasses the Spring proxy. As a result, the transaction management provided by @Transactional is not applied to the inner method, and Hibernate’s session management may not work as expected.

    //
    @Transactional
    @Override
    public void checkUserReport(Object authenticatedUser, Long reportId) throws UserMustBeSameWithReportUserException {
        Report reportOfMessage = findReportById(reportId);
        UserEntity userOfReportMessage = reportOfMessage.getUser();
        if (!authenticatedUser.equals(userOfReportMessage)) {
            throw new UserMustBeSameWithReportUserException("Message of Hacker must be same with report's Hacker");
        }
    }

    @Transactional
    @Override
    public void checkCompanyReport(Object authenticatedUser,  Long reportId) throws UserMustBeSameWithReportUserException {
        Report reportOfMessage = findReportById(reportId);

        CompanyEntity companyOfReportMessage = reportOfMessage.getBugBountyProgram().getCompany();
        if (!authenticatedUser.equals(companyOfReportMessage)) {
            throw new UserMustBeSameWithReportUserException("Message of Company must be same with report's Company");
        }
    }

    @Override
    public Report findReportById(Long reportId) throws ResourceNotFoundException {
        return reportsRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));
    }


}
