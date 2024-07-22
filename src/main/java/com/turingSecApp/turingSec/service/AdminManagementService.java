package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.helper.entityHelper.admin.IAdminEntityHelper;
import com.turingSecApp.turingSec.model.entities.user.AdminEntity;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.payload.user.LoginRequest;
import com.turingSecApp.turingSec.response.admin.AdminAuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class  AdminManagementService {
    private final PasswordEncoder passwordEncoder;
    private final IAdminEntityHelper adminEntityHelper;

    public AdminAuthResponse loginAdmin(LoginRequest user) {
        AdminEntity adminEntity = adminEntityHelper.getAdminEntity(user.getUsernameOrEmail());
        return handleAuthentication(user.getPassword(), adminEntity);
    }

    public String approveCompanyRegistration(Long companyId) {
        String generatedPassword = adminEntityHelper.approveCompanyAndGeneratePass(companyId);
        return handleGeneratedPassword(companyId, generatedPassword);
    }




    private AdminAuthResponse handleAuthentication(String password, AdminEntity adminEntity) {
        if (isAuthenticated(password, adminEntity)) {
            return adminEntityHelper.createAuthResponse(adminEntity);
        } else {
            throw new BadCredentialsException("Invalid username/email or password.");
        }
    }

    private boolean isAuthenticated(String rawPassword, AdminEntity adminEntity) {
        return adminEntity != null && passwordEncoder.matches(rawPassword, adminEntity.getPassword());
    }

    private String handleGeneratedPassword(Long companyId, String generatedPassword) {
        if (generatedPassword != null) {
            CompanyEntity company = adminEntityHelper.getCompanyById(companyId);
            adminEntityHelper.notifyCompanyForApproval(company, generatedPassword);
            return generatedPassword;
        } else {
            throw new RuntimeException("Failed to approve company registration."); //Custom exception yaradaraq onu gondermek lazimdir
        }
    }

}
