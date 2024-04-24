package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.Request.LoginRequest;
import com.turingSecApp.turingSec.dao.entities.AdminEntity;
import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
import com.turingSecApp.turingSec.dao.repository.AdminRepository;
import com.turingSecApp.turingSec.exception.custom.UnauthorizedException;
import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.response.AdminAuthResponse;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.service.AdminService;
import com.turingSecApp.turingSec.service.CompanyService;
import com.turingSecApp.turingSec.service.user.CustomUserDetails;
import com.turingSecApp.turingSec.util.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    
//    @PostMapping("/register")
//    public ResponseEntity<?> registerAdmin(@RequestBody AdminEntity admin) {
//        ResponseEntity<?> registerAdmin = adminService.registerAdmin(admin);
//        return new ResponseEntity<>(registerAdmin, HttpStatus.CREATED);
//    }
    
    // Other admin management endpoints


    @PostMapping("/approve-company/{companyId}")
    public BaseResponse<?> approveCompanyRegistration(@PathVariable Long companyId) {
        // Assuming you have a method in the CompanyService to approve company registration
        String generatedPassword = companyService.approveCompanyRegistration(companyId);
        if (generatedPassword != null) {
            return BaseResponse.success("Company registration approved successfully. Generated password: " + generatedPassword);
        } else {
           throw new RuntimeException("Failed to approve company registration.");
        }
    }


    @PostMapping("/login")
    public BaseResponse<AdminAuthResponse> loginAdmin(@RequestBody LoginRequest user) {
        // Check if the input is an email
        AdminEntity adminEntity = adminRepository.findByEmail(user.getUsernameOrEmail());

        // If the input is not an email, check if it's a username
        if (adminEntity == null) {
            adminEntity = adminRepository.findByUsername(user.getUsernameOrEmail());
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
