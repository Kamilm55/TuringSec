package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.payload.LoginRequest;
import com.turingSecApp.turingSec.dao.entities.AdminEntity;
import com.turingSecApp.turingSec.dao.entities.CompanyEntity;
import com.turingSecApp.turingSec.dao.repository.AdminRepository;
import com.turingSecApp.turingSec.dao.repository.CompanyRepository;
import com.turingSecApp.turingSec.exception.custom.CompanyNotFoundException;
import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.response.AdminAuthResponse;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.service.AdminService;
import com.turingSecApp.turingSec.service.CompanyService;
import com.turingSecApp.turingSec.service.EmailNotificationService;
import com.turingSecApp.turingSec.service.interfaces.IAdminService;
import com.turingSecApp.turingSec.service.user.CustomUserDetails;
import com.turingSecApp.turingSec.util.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class AdminController {
    private final IAdminService adminService;

    @PostMapping("/approve-company/{companyId}")
    public BaseResponse<?> approveCompanyRegistration(@PathVariable Long companyId) {
        return BaseResponse.success("Company registration approved successfully. Generated password: " + adminService.approveCompanyRegistration(companyId));
    }



    @PostMapping("/login")
    public BaseResponse<AdminAuthResponse> loginAdmin(@RequestBody @Valid LoginRequest user) {
       return BaseResponse.success(adminService.loginAdmin(user));
    }

}
