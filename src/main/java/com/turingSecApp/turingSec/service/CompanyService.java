package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.model.entities.user.AdminEntity;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.enums.Role;
import com.turingSecApp.turingSec.model.repository.AdminRepository;
import com.turingSecApp.turingSec.model.repository.CompanyRepository;
import com.turingSecApp.turingSec.exception.custom.EmailAlreadyExistsException;
import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.payload.company.CompanyLoginPayload;
import com.turingSecApp.turingSec.payload.company.RegisterCompanyPayload;
import com.turingSecApp.turingSec.response.company.CompanyResponse;
import com.turingSecApp.turingSec.service.interfaces.ICompanyService;
import com.turingSecApp.turingSec.service.user.CustomUserDetails;
import com.turingSecApp.turingSec.service.user.UserService;
import com.turingSecApp.turingSec.service.user.factory.UserFactory;
import com.turingSecApp.turingSec.util.UtilService;
import com.turingSecApp.turingSec.util.mapper.CompanyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.turingSecApp.turingSec.model.enums.Role.ROLE_COMPANY;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyService implements ICompanyService {
    private final UserService userService;
    private final UserFactory userFactory;
    private final EmailNotificationService EmailNotificationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtTokenProvider;
    private final UtilService utilService;
    private final CompanyRepository companyRepository;

    private final AdminRepository adminRepository;

    @Override
    public void registerCompany(RegisterCompanyPayload companyPayload) {
        log.info(String.format("Company payload: %s", companyPayload));
        // Ensure the company doesn't already exist
        checkCompanyEmailUnique(companyPayload.getEmail());


        CompanyEntity company = buildCompanyEntity(companyPayload);
        log.info(String.format("Company object before save: %s", company));

        // Save the company
        CompanyEntity savedCompany = companyRepository.save(company);
        log.info(String.format("Saved company: %s", savedCompany));
        // Notify admins for approval
        notifyAdminsForApproval(savedCompany);
    }
    // Method to check if the company email is unique
    private void checkCompanyEmailUnique(String email) {
        if (companyRepository.findByEmail(email) != null) {
            throw new EmailAlreadyExistsException("Email is already taken.");
        }
    }


    // Method to build the CompanyEntity
    private CompanyEntity buildCompanyEntity(RegisterCompanyPayload companyPayload) {
        return CompanyEntity.builder()
                .job_title(companyPayload.getJobTitle())
                .company_name(companyPayload.getCompanyName())
                .email(companyPayload.getEmail())
                .message(companyPayload.getMessage())
                .first_name(companyPayload.getFirstName())
                .last_name(companyPayload.getLastName())
                .activated(false)
                .roles(Collections.singleton(ROLE_COMPANY))
                .build();
    }

    @Override
    public Map<String, String> loginCompany(CompanyLoginPayload companyLoginPayload) {
        // Check if the input is an email
        CompanyEntity companyEntity = companyRepository.findByEmail(companyLoginPayload.getEmail());
        // Authenticate user if found
        if (companyEntity != null && passwordEncoder.matches(companyLoginPayload.getPassword(), companyEntity.getPassword())) {
            // Generate token using the user details
            UserDetails userDetails = new CustomUserDetails(companyEntity);
            String token = jwtTokenProvider.generateToken(userDetails);

            String userId = ((CustomUserDetails) userDetails).getId();

            // Create a response map containing the token and user ID
            Map<String, String> response = new HashMap<>();
            response.put("access_token", token);
            response.put("companyId", String.valueOf(userId));
            return response;
        } else {
            throw new BadCredentialsException("Invalid username/email or password.");
        }
    }
    @Override
    public List<CompanyResponse> getAllCompanies() {
        List<CompanyEntity> companyEntities = companyRepository.findAll();
        return companyEntities.stream().map(CompanyMapper.INSTANCE::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public CompanyResponse getCompaniesById(String id) {
        CompanyEntity company = utilService.findCompanyById(id);
        return CompanyMapper.INSTANCE.convertToResponse(company);
    }

    @Override
    public CompanyResponse getCurrentUser() {
        // Retrieve user details from the database
        CompanyEntity company = (CompanyEntity) userFactory.getAuthenticatedBaseUser();
        return CompanyMapper.INSTANCE.convertToResponse(company);
    }


    // Utils
    private void notifyAdminsForApproval(CompanyEntity company) {
        // Get a list of administrators from the database or any other source
        List<AdminEntity> admins = adminRepository.findAll(); // Assuming you have an AdminRepository

        // Compose the email message
        String subject = "New Company Registration for Approval";
        String content = "A new company has registered and requires approval.\n\n"
                + "Company ID: " + company.getId() + "\n"
                + "Company Name: " + company.getCompany_name() + "\n"
                + "Contact Person: " + company.getEmail() + "\n"
                + "Name , Surname: " + company.getFirst_name() + ", " + company.getLast_name() + "\n"
                + "Job Title: " + company.getJob_title() + "\n\n"
                + "Message: " + company.getMessage() + "\n\n"
                + "Please login to the admin panel to review and approve.";

        // Send email notification to each admin
        for (AdminEntity admin : admins) {
            log.info(String.format("Sent email of admins: %s", admin.getEmail()));
            EmailNotificationService.sendEmail(admin.getEmail(), subject, content);
        }
    }



}
