package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.model.entities.user.AdminEntity;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.repository.AdminRepository;
import com.turingSecApp.turingSec.model.repository.CompanyRepository;
import com.turingSecApp.turingSec.exception.custom.CompanyNotFoundException;
import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.payload.user.LoginRequest;
import com.turingSecApp.turingSec.response.admin.AdminAuthResponse;
import com.turingSecApp.turingSec.service.interfaces.IAdminService;
import com.turingSecApp.turingSec.service.user.CustomUserDetails;
import com.turingSecApp.turingSec.util.UtilService;
import com.turingSecApp.turingSec.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService implements IAdminService {
    private final EmailNotificationService EmailNotificationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtTokenProvider;
    private final AdminRepository adminRepository;
    private final UtilService utilService;

    private final CompanyRepository companyRepository;

    @Override
    public AdminAuthResponse loginAdmin(LoginRequest loginRequest) {
        // Check if the input is an email
        AdminEntity adminEntity = adminRepository.findByEmail(loginRequest.getUsernameOrEmail());
        log.info("Login Payload for admin: " + loginRequest);

        // If the input is not an email, check if it's a username
        if (adminEntity == null) {
            adminEntity = adminRepository.findByUsername(loginRequest.getUsernameOrEmail()).orElseThrow(()->new UserNotFoundException("Admin does not found with username:" + loginRequest.getUsernameOrEmail() ));
        }

        // Authenticate user if found
        if (adminEntity != null && passwordEncoder.matches(loginRequest.getPassword(), adminEntity.getPassword())) {
            // Generate token using the user details
            UserDetails userDetails = new CustomUserDetails(adminEntity);
            String token = jwtTokenProvider.generateToken(userDetails);


            return AdminAuthResponse.builder()
                    .accessToken(token)
                    .userInfo(UserMapper.INSTANCE.convert(adminEntity))
                    .build();
        } else {
            // Authentication failed
            throw new BadCredentialsException("Invalid username/email or password.");
        }
    }
    @Override
    public String approveCompanyRegistration(String companyId) {
        // Assuming you have a method in the CompanyService to approve company registration
        String generatedPassword = approveCompanyAndGeneratePass(companyId);

        if (generatedPassword != null) {
            CompanyEntity company = companyRepository.findById(utilService.convertToUUID(companyId)).orElseThrow(()-> new CompanyNotFoundException("Company not found with id:" + companyId));
            notifyCompanyForApproval(company,generatedPassword);

            return  generatedPassword;
        } else {
            throw new RuntimeException("Failed to approve company registration.");
        }
    }



    // Util
    public String approveCompanyAndGeneratePass(String companyId) {
        Optional<CompanyEntity> companyOptional = companyRepository.findById(utilService.convertToUUID(companyId));
            if (companyOptional.isPresent()) {
                CompanyEntity company = companyOptional.get();

                // Generate a random password for the company
                String generatedPassword = generateRandomPassword();
                company.setPassword(passwordEncoder.encode(generatedPassword));

                // Set the approval status to true
                company.setActivated(true);

                // Save the company
                companyRepository.save(company);

                // Return the generated password
                return generatedPassword;
            } else {
                throw new CompanyNotFoundException("Company with the given ID not found.");
            }
    }


    private String generateRandomPassword() {
        // Generate a random alphanumeric password with 12 characters
        return RandomStringUtils.randomAlphanumeric(12);
    }


    public void notifyCompanyForApproval(CompanyEntity company , String pass) {

        // Compose the email message
        String subject = "Company Registration Approval from TuringSec";
        String content = "Congratulations! We accepted your company request.\n\n"
                + "Please login to the app with this generated password: " + pass ;

        // Send email notification to the company
        EmailNotificationService.sendEmail(company.getEmail(), subject, content);
    }
}
