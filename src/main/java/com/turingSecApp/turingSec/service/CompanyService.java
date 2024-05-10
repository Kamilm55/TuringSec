package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.dao.entities.AdminEntity;
import com.turingSecApp.turingSec.dao.entities.CompanyEntity;
import com.turingSecApp.turingSec.dao.entities.role.Role;
import com.turingSecApp.turingSec.dao.repository.AdminRepository;
import com.turingSecApp.turingSec.dao.repository.CompanyRepository;
import com.turingSecApp.turingSec.dao.repository.RoleRepository;
import com.turingSecApp.turingSec.exception.custom.EmailAlreadyExistsException;
import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.payload.company.CompanyLoginPayload;
import com.turingSecApp.turingSec.payload.company.RegisterCompanyPayload;
import com.turingSecApp.turingSec.response.company.CompanyResponse;
import com.turingSecApp.turingSec.service.interfaces.ICompanyService;
import com.turingSecApp.turingSec.service.user.CustomUserDetails;
import com.turingSecApp.turingSec.service.user.UserService;
import com.turingSecApp.turingSec.util.UtilService;
import com.turingSecApp.turingSec.util.mapper.CompanyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService implements ICompanyService {
    private final UserService userService;
    private final IEmailNotificationService IEmailNotificationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtTokenProvider;
    private final UtilService utilService;
    private final CompanyRepository companyRepository;

    private final RoleRepository roleRepository;
    private final AdminRepository adminRepository;

    @Override
    public void registerCompany(RegisterCompanyPayload companyPayload) {
        // Ensure the company doesn't already exist
        checkCompanyEmailUnique(companyPayload.getEmail());

        // Set the "COMPANY" role for the company
        Role companyRole = findCompanyRole();

        CompanyEntity company = buildCompanyEntity(companyPayload, companyRole);

        // Save the company
        CompanyEntity savedCompany = companyRepository.save(company);

        // Notify admins for approval
        notifyAdminsForApproval(savedCompany);
    }

    // Method to check if the company email is unique
    private void checkCompanyEmailUnique(String email) {
        if (companyRepository.findByEmail(email) != null) {
            throw new EmailAlreadyExistsException("Email is already taken.");
        }
    }

    // Method to find the "COMPANY" role
    private Role findCompanyRole() {
        Role companyRole = roleRepository.findByName("COMPANY");
        if (companyRole == null) {
            throw new NotFoundException("Company role not found.");
        }
        return companyRole;
    }

    // Method to build the CompanyEntity
    private CompanyEntity buildCompanyEntity(RegisterCompanyPayload companyPayload, Role companyRole) {
        return CompanyEntity.builder()
                .job_title(companyPayload.getJobTitle())
                .company_name(companyPayload.getCompanyName())
                .email(companyPayload.getEmail())
                .message(companyPayload.getMessage())
                .first_name(companyPayload.getFirstName())
                .last_name(companyPayload.getLastName())
                .approved(false)
                .roles(Collections.singleton(companyRole))
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

            Long userId = ((CustomUserDetails) userDetails).getId();

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
    public CompanyResponse getCompaniesById(Long id) {
        CompanyEntity company = userService.getCompaniesById(id);

        return CompanyMapper.INSTANCE.convertToResponse(company);
    }

    @Override
    public CompanyResponse getCurrentUser() {
        // Retrieve user details from the database
        CompanyEntity company = utilService.getAuthenticatedCompany();

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
            IEmailNotificationService.sendEmail(admin.getEmail(), subject, content);
        }
    }


}
