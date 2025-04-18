package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.payload.company.CompanyLoginPayload;
import com.turingSecApp.turingSec.payload.company.CompanyUpdateRequest;
import com.turingSecApp.turingSec.payload.company.RegisterCompanyPayload;
import com.turingSecApp.turingSec.payload.user.ChangeEmailRequest;
import com.turingSecApp.turingSec.payload.user.ChangePasswordRequest;
import com.turingSecApp.turingSec.response.company.CompanyResponse;
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
    public BaseResponse<CompanyResponse> getCompaniesById(@PathVariable String id) {
        return BaseResponse.success(companyService.getCompaniesById(id));
    }


    @GetMapping("/current-user")
    public BaseResponse<CompanyResponse> getCurrentUser() {
       return BaseResponse.success(companyService.getCurrentUser());
    }

    @PatchMapping("/change-password")
    public BaseResponse<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        companyService.changePassword(request);
        return BaseResponse.success(null,"Password updated successfully");
    }

    @PatchMapping("/change-email")
    public BaseResponse<?>  changeEmail(@Valid @RequestBody ChangeEmailRequest request) {
        companyService.changeEmail(request);
        return BaseResponse.success(null,"Email updated successfully");
    }

    @PutMapping("/update-profile")
    public BaseResponse<CompanyResponse> updateProfile( @Valid @RequestBody CompanyUpdateRequest companyUpdateRequest) {
        return BaseResponse.success(companyService.updateCompany(companyUpdateRequest));
    }

    @PatchMapping("/close-account")
    public BaseResponse<?> closeAccount(){
        companyService.closeAccount();
        return BaseResponse.success(null,"Account deleted successfully");
    }
}
