package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.payload.CompanyLoginPayload;
import com.turingSecApp.turingSec.payload.RegisterCompanyPayload;
import com.turingSecApp.turingSec.response.CompanyResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;


public interface ICompanyService {
    void registerCompany(RegisterCompanyPayload registerCompanyPayload);
    Map<String, String> loginCompany(@RequestBody @Valid CompanyLoginPayload companyLoginPayload);
    List<CompanyResponse> getAllCompanies();
    CompanyResponse getCompaniesById(@PathVariable Long id);
    CompanyResponse getCurrentUser();
}
