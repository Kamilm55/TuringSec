package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.payload.company.CompanyLoginPayload;
import com.turingSecApp.turingSec.payload.company.RegisterCompanyPayload;
import com.turingSecApp.turingSec.payload.company.UpdateCompanyPayload;
import com.turingSecApp.turingSec.response.company.CompanyResponse;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.response.user.UserHackerDTO;
import com.turingSecApp.turingSec.service.interfaces.ICompanyService;
import com.turingSecApp.turingSec.service.user.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyController {

    final ICompanyService companyService;
    final UserService userService;

    @PostMapping("/register")
    public BaseResponse<?> registerCompany(@RequestBody @Valid RegisterCompanyPayload registerCompanyPayload) {
        companyService.registerCompany(registerCompanyPayload);

        return BaseResponse.success(null,"Your request sent to admins. When any admin approve your request, you receive password for company from gmail!");
    }


    @PostMapping("/login")
    public BaseResponse<Map<String, String>> loginCompany(@RequestBody @Valid CompanyLoginPayload companyLoginPayload) {
       return BaseResponse.success(companyService.loginCompany(companyLoginPayload));
    }

    @GetMapping
    public BaseResponse<List<CompanyResponse>> getAllCompanies() {
        return BaseResponse.success(companyService.getAllCompanies());
    }

    @GetMapping("/{id}")
    public BaseResponse<CompanyResponse> getCompaniesById(@PathVariable Long id) {
        return BaseResponse.success(companyService.getCompaniesById(id));
    }


    @GetMapping("/current-user")
    public BaseResponse<CompanyResponse> getCurrentUser() {
       return BaseResponse.success(companyService.getCurrentUser());
    }

    @PutMapping(path = "/update")
    public BaseResponse<CompanyResponse> updateCompany(@Valid @RequestBody UpdateCompanyPayload updateCompanyPayload){
        CompanyResponse updateCompany = companyService.updateCompany(updateCompanyPayload);
        String newToken = userService.generateCompanyNewToken(updateCompany.getEmail());
        return BaseResponse.success(updateCompany,
                "Company updated successfully. You must update Authorization header (Bearer token) , new token is: "  + newToken
                );
    }


}
