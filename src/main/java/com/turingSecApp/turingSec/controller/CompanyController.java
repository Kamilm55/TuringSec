package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.payload.CompanyRequest;
import com.turingSecApp.turingSec.dao.entities.CompanyEntity;
import com.turingSecApp.turingSec.dao.repository.CompanyRepository;
import com.turingSecApp.turingSec.exception.custom.UnauthorizedException;
import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.payload.RegisterCompanyPayload;
import com.turingSecApp.turingSec.response.CompanyResponse;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.service.CompanyService;
import com.turingSecApp.turingSec.service.user.CustomUserDetails;
import com.turingSecApp.turingSec.service.user.UserService;
import com.turingSecApp.turingSec.util.CompanyMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class CompanyController {
    private final UserService userService;
    private final CompanyService companyService;
    private final PasswordEncoder passwordEncoder;
    private final CompanyRepository companyRepository;
    private final JwtUtil jwtTokenProvider;

    @PostMapping("/register")
    public BaseResponse<?> registerCompany(@RequestBody @Valid RegisterCompanyPayload registerCompanyPayload) {
       companyService.registerCompany(registerCompanyPayload);

        return BaseResponse.success(null,"Your request sent to admins. When any admin approve your request, you receive password for company from gmail!");
    }


    @PostMapping("/login")
    public BaseResponse<Map<String, String>> loginCompany(@RequestBody @Valid CompanyRequest companyRequest) {
        // Check if the input is an email
        CompanyEntity companyEntity = companyRepository.findByEmail(companyRequest.getEmail());

        // Authenticate user if found
        if (companyEntity != null && passwordEncoder.matches(companyRequest.getPassword(), companyEntity.getPassword())) {
            // Generate token using the user details
            UserDetails userDetails = new CustomUserDetails(companyEntity);
            String token = jwtTokenProvider.generateToken(userDetails);

            Long userId = ((CustomUserDetails) userDetails).getId();

            // Create a response map containing the token and user ID
            Map<String, String> response = new HashMap<>();
            response.put("access_token", token);
            response.put("companyId", String.valueOf(userId));


            return BaseResponse.success(response);
        } else {
            throw new BadCredentialsException("Invalid username/email or password.");
        }
    }



    @GetMapping
    public BaseResponse<List<CompanyResponse>> getAllCompanies() {
        List<CompanyEntity> companyEntities = userService.getAllCompanies();
        List<CompanyResponse> companyResponses = companyEntities.stream().map(CompanyMapper.INSTANCE::convertToResponse).collect(Collectors.toList());

        return BaseResponse.success(companyResponses);
    }

    @GetMapping("/{id}")
    public BaseResponse<CompanyResponse> getCompaniesById(@PathVariable Long id) {
        CompanyEntity company = userService.getCompaniesById(id);

        return BaseResponse.success(CompanyMapper.INSTANCE.convertToResponse(company));
    }


    @GetMapping("/current-user")
    public BaseResponse<CompanyResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();

            // Retrieve user details from the database
            CompanyEntity company = companyRepository.findByEmail(email);
            CompanyResponse companyResponse = CompanyMapper.INSTANCE.convertToResponse(company);

            return BaseResponse.success(companyResponse);
        } else {
            throw new UnauthorizedException();
        }
    }


}
