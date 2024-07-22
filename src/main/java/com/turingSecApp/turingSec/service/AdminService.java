package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.model.entities.user.AdminEntity;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.entities.role.Role;
import com.turingSecApp.turingSec.model.repository.AdminRepository;
import com.turingSecApp.turingSec.model.repository.CompanyRepository;
import com.turingSecApp.turingSec.model.repository.RoleRepository;
import com.turingSecApp.turingSec.exception.custom.CompanyNotFoundException;
import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.payload.user.LoginRequest;
import com.turingSecApp.turingSec.response.admin.AdminAuthResponse;
import com.turingSecApp.turingSec.service.interfaces.IAdminService;
import com.turingSecApp.turingSec.service.user.CustomUserDetails;
import com.turingSecApp.turingSec.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService implements IAdminService {
    private final AdminManagementService adminManagementService;

    @Override
    public AdminAuthResponse loginAdmin(LoginRequest user) {
        return adminManagementService.loginAdmin(user);
    }

    @Override
    public String approveCompanyRegistration(Long companyId) {
        return adminManagementService.approveCompanyRegistration(companyId);
    }

}
