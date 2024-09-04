package com.turingSecApp.turingSec.util;

import com.turingSecApp.turingSec.config.websocket.security.CustomWebsocketSecurityContext;
import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.model.entities.user.*;
import com.turingSecApp.turingSec.model.repository.*;
import com.turingSecApp.turingSec.exception.custom.*;
import com.turingSecApp.turingSec.model.repository.program.ProgramRepository;
import com.turingSecApp.turingSec.model.repository.report.ReportRepository;
import com.turingSecApp.turingSec.response.program.BugBountyProgramWithAssetTypeDTO;
import com.turingSecApp.turingSec.response.user.AuthResponse;
import com.turingSecApp.turingSec.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UtilService {
    private final ReportRepository reportRepository;
    private final CompanyRepository companyRepository;
    private final AdminRepository adminRepository;
    private final ProgramRepository programRepository;
    private final UserRepository userRepository;
    private final BaseUserRepository baseUserRepository;
    private final CustomWebsocketSecurityContext websocketSecurityContext;

    public String generateActivationToken() {
        // You can implement your own token generation logic here
        // This could involve creating a unique token, saving it in the database,
        // and associating it with the user for verification during activation.
        // For simplicity, you can use a library like java.util.UUID.randomUUID().
        return UUID.randomUUID().toString();
    }
    // Method to build authentication response
    public AuthResponse buildAuthResponse(String token, UserEntity user, HackerEntity hacker) {
        return AuthResponse.builder()
                .accessToken(token)
                .userInfo(UserMapper.INSTANCE.toDto(user, hacker))
                .build();
    }

    public UUID convertToUUID(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidUUIDFormatException("Invalid UUID format for ID: " + id, e);
        }
    }

    public void isUserExistWithEmail(String email) {
//        System.out.println(email);
        if (userRepository.findByEmail(email) != null) {
            throw new EmailAlreadyExistsException("Email is already taken.");
        }
    }

    public void isUserExistWithUsername(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("Username is already taken.");
        }
    }
    // Method to check if the user is activated
    public void checkUserIsActivated(UserEntity userEntity) {
        if (!userEntity.isActivated()) {
            throw new UserNotActivatedException("User is not activated yet.");
        }
    }


    // Check report belongs to specific user or not
    @Transactional
    public void checkUserReport(BaseUser authenticatedUser, Long reportId) throws UserMustBeSameWithReportUserException {
        Report reportOfMessage = findReportById(reportId);
        UserEntity userOfReportMessage = reportOfMessage.getUser();

        if (!authenticatedUser.equals(userOfReportMessage)) {
            throw new UserMustBeSameWithReportUserException("Message of Hacker must be same with report's Hacker");
        }
    }

    @Transactional
    public void checkCompanyReport(BaseUser authenticatedUser,  Long reportId) throws UserMustBeSameWithReportUserException {
        Report reportOfMessage = findReportById(reportId);
        CompanyEntity companyOfReportMessage = reportOfMessage.getBugBountyProgram().getCompany();

        if (!authenticatedUser.equals(companyOfReportMessage)) {
            throw new UserMustBeSameWithReportUserException("Message of Company must be same with report's Company");
        }
    }

    // Find entity by id or else throw exception
    public CompanyEntity findCompanyById(String id) {
        Optional<CompanyEntity> companyEntity = companyRepository.findById(convertToUUID(id));
        return companyEntity.orElseThrow(() -> new ResourceNotFoundException("Company not found with id:" + id));
    }

    public BaseUser findBaseUserById(String id) {
        return baseUserRepository.findById(convertToUUID(id)).orElseThrow(()-> new UserNotFoundException("Base User not found with this baseUserId: " + id));

    }
    public UserEntity findUserById(String id) {
        return userRepository.findById(convertToUUID(id)).orElseThrow(()-> new UserNotFoundException("User not found with this baseUserId: " + id));

    }
    public AdminEntity findAdminById(String id) {
        return adminRepository.findById(convertToUUID(id)).orElseThrow(()-> new UserNotFoundException("Admin not found with this baseUserId: " + id));

    }
    public Program findProgramById(Long id) {
        return programRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bug Bounty Program not found with id:" + id));
    }
    public Report findReportById(Long id) {
        return reportRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bug Bounty Report not found with id:" + id));
    }

    // For mock data
    public void setStaticUUIDForUsers(BaseUser user) {
        // Static id for easy testing in local development
        // Users/hackers
        if(user.getEmail().equals("mockhacker1@gmail.com"))
            user.setId("191ded5d-148b-446d-8069-e8a8bd8c7ec7");

        if(user.getEmail().equals("mockhacker2@gmail.com"))
            user.setId("ce3ab950-8cd1-550a-89d7-a3e31801e660");

        // Company
        if(user.getEmail().equals("string@gmail.com"))
            user.setId("bfe8e29f-bb4b-5bf2-a083-556e365e91a2");

        // Admins
        if(user.getEmail().equals("kamilmmmdov2905@gmail.com"))
            user.setId("69be6e91-e268-5df1-925a-3e3c7106408c");
        if(user.getEmail().equals("elnarzulfuqarli2001@gmail.com"))
            user.setId("ff34ed07-a4c3-5528-a2fd-daa8735a13de");
    }
}
