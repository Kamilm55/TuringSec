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
    private final AdminService adminService;
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;
    private final CompanyService companyService;
    private final JwtUtil jwtTokenProvider;
    private final CompanyRepository companyRepository;

    @PostMapping("/approve-company/{companyId}")
    public BaseResponse<?> approveCompanyRegistration(@PathVariable Long companyId) {
        // Assuming you have a method in the CompanyService to approve company registration
        String generatedPassword = companyService.approveCompanyRegistration(companyId);

        if (generatedPassword != null) {
            CompanyEntity company = companyRepository.findById(companyId).orElseThrow(()-> new CompanyNotFoundException("Company not found with id:" + companyId));
            adminService.notifyCompanyForApproval(company,generatedPassword);

            return BaseResponse.success("Company registration approved successfully. Generated password: " + generatedPassword);
        } else {
           throw new RuntimeException("Failed to approve company registration.");
        }
    }



    @PostMapping("/login")
    public BaseResponse<AdminAuthResponse> loginAdmin(@RequestBody @Valid LoginRequest user) {
        // Check if the input is an email
        AdminEntity adminEntity = adminRepository.findByEmail(user.getUsernameOrEmail());

        // If the input is not an email, check if it's a username
        if (adminEntity == null) {
             adminEntity = adminRepository.findByUsername(user.getUsernameOrEmail()).orElseThrow(()->new UsernameNotFoundException("Admin does not found with username:" + user.getUsernameOrEmail() ));
        }

        // Authenticate user if found
        if (adminEntity != null && passwordEncoder.matches(user.getPassword(), adminEntity.getPassword())) {
            // Generate token using the user details
            UserDetails userDetails = new CustomUserDetails(adminEntity);
            String token = jwtTokenProvider.generateToken(userDetails);


            return BaseResponse.success(AdminAuthResponse.builder()
                    .accessToken(token)
                    .userInfo(UserMapper.INSTANCE.convert(adminEntity))
                    .build());
        } else {
            // Authentication failed
            throw new BadCredentialsException("Invalid username/email or password.");
        }
    }

}
