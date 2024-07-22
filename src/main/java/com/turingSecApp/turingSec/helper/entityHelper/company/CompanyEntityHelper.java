package com.turingSecApp.turingSec.helper.entityHelper.company;

import com.turingSecApp.turingSec.exception.custom.EmailAlreadyExistsException;
import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.model.entities.role.Role;
import com.turingSecApp.turingSec.model.entities.user.AdminEntity;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.repository.AdminRepository;
import com.turingSecApp.turingSec.model.repository.CompanyRepository;
import com.turingSecApp.turingSec.model.repository.RoleRepository;
import com.turingSecApp.turingSec.payload.company.RegisterCompanyPayload;
import com.turingSecApp.turingSec.service.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CompanyEntityHelper implements ICompanyEntityHelper{
    private final JwtUtil jwtTokenProvider;


    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;
    private final AdminRepository adminRepository;
    private final com.turingSecApp.turingSec.service.EmailNotificationService EmailNotificationService;

    // Method to check if the company email is unique
    @Override
    public void checkCompanyEmailUnique(String email) {
        if (companyRepository.findByEmail(email) != null) {
            throw new EmailAlreadyExistsException("Email is already taken.");
        }
    }

    // Method to find the "COMPANY" role
    @Override
    public Role findCompanyRole() {
        Role companyRole = roleRepository.findByName("COMPANY");
        if (companyRole == null) {
            throw new NotFoundException("Company role not found.");
        }
        return companyRole;
    }

    // Method to build the CompanyEntity
    @Override
    public CompanyEntity buildCompanyEntity(RegisterCompanyPayload companyPayload, Role companyRole) {
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

    // Utils
    @Override
    public void notifyAdminsForApproval(CompanyEntity company) {
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
            EmailNotificationService.sendEmail(admin.getEmail(), subject, content);
        }
    }
    @Override
    public Map<String, String> createResponse(CompanyEntity companyEntity) {
        UserDetails userDetails = new CustomUserDetails(companyEntity);
        String token = jwtTokenProvider.generateToken(userDetails);

        Long userId = ((CustomUserDetails) userDetails).getId();

        // Create a response map containing the token and user ID
        Map<String, String> response = new HashMap<>();
        response.put("access_token", token);
        response.put("companyId", String.valueOf(userId));

        return response;
    }
}
