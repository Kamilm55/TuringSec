package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.payload.CompanyLoginPayload;
import com.turingSecApp.turingSec.payload.RegisterCompanyPayload;
import com.turingSecApp.turingSec.response.CompanyResponse;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.service.interfaces.ICompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class CompanyController {
    private final ICompanyService companyService;

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


}
