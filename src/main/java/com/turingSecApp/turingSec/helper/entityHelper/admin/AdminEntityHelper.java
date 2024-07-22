package com.turingSecApp.turingSec.helper.entityHelper.admin;

import com.turingSecApp.turingSec.exception.custom.CompanyNotFoundException;
import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.model.entities.role.Role;
import com.turingSecApp.turingSec.model.entities.user.AdminEntity;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.repository.AdminRepository;
import com.turingSecApp.turingSec.model.repository.CompanyRepository;
import com.turingSecApp.turingSec.model.repository.RoleRepository;
import com.turingSecApp.turingSec.response.admin.AdminAuthResponse;
import com.turingSecApp.turingSec.service.EmailNotificationService;
import com.turingSecApp.turingSec.service.user.CustomUserDetails;
import com.turingSecApp.turingSec.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminEntityHelper implements IAdminEntityHelper{

    private final AdminRepository adminRepository;
    private final JwtUtil jwtTokenProvider;


    private final EmailNotificationService EmailNotificationService;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public String approveCompanyAndGeneratePass(Long companyId) {
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

    @Override
    public AdminEntity getAdminEntity(String usernameOrEmail) {
        AdminEntity adminEntity = adminRepository.findByEmail(usernameOrEmail);

        if (adminEntity == null) {
            adminEntity = adminRepository.findByUsername(usernameOrEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("Admin not found with username: " + usernameOrEmail));
        }
        return adminEntity;
    }

    @Override
    public AdminAuthResponse createAuthResponse(AdminEntity adminEntity) {
        UserDetails userDetails = new CustomUserDetails(adminEntity);
        String token = jwtTokenProvider.generateToken(userDetails);

        return AdminAuthResponse.builder()
                .accessToken(token)
                .userInfo(UserMapper.INSTANCE.convert(adminEntity))
                .build();
    }

    @Override
    public CompanyEntity getCompanyById(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with id: " + companyId));
    }

}
