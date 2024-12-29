package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.payload.company.CompanyLoginPayload;
import com.turingSecApp.turingSec.payload.company.CompanyUpdateRequest;
import com.turingSecApp.turingSec.payload.company.RegisterCompanyPayload;
import com.turingSecApp.turingSec.payload.user.ChangeEmailRequest;
import com.turingSecApp.turingSec.payload.user.ChangePasswordRequest;
import com.turingSecApp.turingSec.response.company.CompanyResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;


public interface ICompanyService {
    void registerCompany(RegisterCompanyPayload registerCompanyPayload);

    Map<String, String> loginCompany(@RequestBody @Valid CompanyLoginPayload companyLoginPayload);

    List<CompanyResponse> getAllCompanies();

    CompanyResponse getCompaniesById(@PathVariable String id);

    CompanyResponse getCurrentUser();

    void changePassword(ChangePasswordRequest request);

    void changeEmail(ChangeEmailRequest request);

    CompanyResponse updateCompany(CompanyUpdateRequest companyUpdateRequest);

    void closeAccount();
}
