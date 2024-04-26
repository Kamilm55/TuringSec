package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.exception.custom.CompanyNotFoundException;
import com.turingSecApp.turingSec.payload.RegisterCompanyPayload;
import com.turingSecApp.turingSec.response.AuthResponse;
import com.turingSecApp.turingSec.response.RegistrationResponse;
import com.turingSecApp.turingSec.dao.entities.AdminEntity;
import com.turingSecApp.turingSec.dao.entities.CompanyEntity;
import com.turingSecApp.turingSec.dao.entities.role.Role;
import com.turingSecApp.turingSec.dao.repository.AdminRepository;
import com.turingSecApp.turingSec.dao.repository.CompanyRepository;
import com.turingSecApp.turingSec.dao.repository.RoleRepository;
import com.turingSecApp.turingSec.exception.custom.EmailAlreadyExistsException;
import com.turingSecApp.turingSec.background_file_upload_for_hacker.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;
    private final EmailNotificationService emailNotificationService;
    private final FileRepository fileRepository;
    //private ModelMapper modelMapper;

    public String approveCompanyRegistration(Long companyId) {
        Optional<CompanyEntity> companyOptional = companyRepository.findById(companyId);
        if (companyOptional.isPresent()) {
            CompanyEntity company = companyOptional.get();

            // Generate a random password for the company
            String generatedPassword = generateRandomPassword();
            company.setPassword(passwordEncoder.encode(generatedPassword));

            // Set the approval status to true
            company.setApproved(true);

            // Retrieve the "COMPANY" role
            Role companyRole = roleRepository.findByName("COMPANY");
            if (companyRole == null) {
                throw new CompanyNotFoundException("Company role not found.");
            }

            // Save the company
            companyRepository.save(company);

            //todo: send password to company gmail with smtp

            // Return the generated password
            return generatedPassword;
        } else {
            throw new CompanyNotFoundException("Company with the given ID not found.");
        }
    }


    public void registerCompany(RegisterCompanyPayload companyPayload) {
        // Ensure the company doesn't already exist
        if (companyRepository.findByEmail(companyPayload.getEmail()) != null) {
            throw new EmailAlreadyExistsException("Email is already taken.");
        }

        // Set the "COMPANY" role for the company
        Role companyRole = roleRepository.findByName("COMPANY");
        if (companyRole == null) {
            throw new NotFoundException("Company role not found.");
        }

        CompanyEntity company = CompanyEntity.builder()
                .job_title(companyPayload.getJobTitle())
                .company_name(companyPayload.getCompanyName())
                .email(companyPayload.getEmail())
                .message(companyPayload.getMessage())
                .first_name(companyPayload.getFirstName())
                .last_name(companyPayload.getLastName())
                .approved(false)
                .build();
        //todo:add default mock data

        company.setRoles(Collections.singleton(companyRole));


        // Save the company
        CompanyEntity savedCompany = companyRepository.save(company);


        notifyAdminsForApproval(savedCompany);
    }


    //
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
            emailNotificationService.sendEmail(admin.getEmail(), subject, content);
        }
    }



    // Other methods for managing companies

    private String generateRandomPassword() {
        // Generate a random alphanumeric password with 12 characters
        return RandomStringUtils.randomAlphanumeric(12);
    }

//    public ResponseEntity<CompanyResponse> getCompanyById(Long companyId) {
//        Optional<CompanyEntity> company = companyRepository.findById(companyId);
//
//        File fileByCompanyId = fileRepository.findFileByCompanyId(companyId);
//
//        CompanyResponse blogPostResponse = modelMapper.map(company.get(), CompanyResponse.class);
//        blogPostResponse.setFileId(fileByCompanyId.getId());
//
//        return ResponseEntity.status(HttpStatus.OK).body(blogPostResponse);
//    }





}
