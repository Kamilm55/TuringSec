package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.helper.entityHelper.company.ICompanyEntityHelper;
import com.turingSecApp.turingSec.model.entities.role.Role;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.repository.CompanyRepository;
import com.turingSecApp.turingSec.payload.company.CompanyLoginPayload;
import com.turingSecApp.turingSec.payload.company.RegisterCompanyPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CompanyManagementService {

    private final ICompanyEntityHelper companyEntityHelper;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;


    public void registerCompany(RegisterCompanyPayload companyPayload) {

        // Ensure the company doesn't already exist
        companyEntityHelper.checkCompanyEmailUnique(companyPayload.getEmail());

        // Set the "COMPANY" role for the company
        Role companyRole = companyEntityHelper.findCompanyRole();

        CompanyEntity company = companyEntityHelper.buildCompanyEntity(companyPayload, companyRole);

        // Save the company
        CompanyEntity savedCompany = companyRepository.save(company);

        // Notify admins for approval
        companyEntityHelper.notifyAdminsForApproval(savedCompany);
    }


    public Map<String, String> loginCompany(CompanyLoginPayload companyLoginPayload) {
        CompanyEntity companyEntity = findCompanyByEmail(companyLoginPayload.getEmail());

        if (isAuthenticated(companyLoginPayload.getPassword(), companyEntity)) {
            return companyEntityHelper.createResponse(companyEntity);
        } else {
            throw new BadCredentialsException("Invalid username/email or password.");
        }
    }

    private boolean isAuthenticated(String password, CompanyEntity companyEntity) {
        return companyEntity != null && passwordEncoder.matches(password, companyEntity.getPassword());
    }

    private CompanyEntity findCompanyByEmail(String email) {
        return companyRepository.findByEmail(email);
    }
}
