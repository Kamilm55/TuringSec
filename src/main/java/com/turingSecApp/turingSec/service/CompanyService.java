package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.payload.company.CompanyLoginPayload;
import com.turingSecApp.turingSec.payload.company.RegisterCompanyPayload;
import com.turingSecApp.turingSec.response.company.CompanyResponse;
import com.turingSecApp.turingSec.service.interfaces.ICompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CompanyService implements ICompanyService {
    private final CompanyManagementService companyManagementService;
    private final CompanyRetrievalService companyRetrievalService;


    @Override
    public void registerCompany(RegisterCompanyPayload companyPayload) {
        companyManagementService.registerCompany(companyPayload);
    }

    @Override
    public Map<String, String> loginCompany(CompanyLoginPayload companyLoginPayload) {
        return companyManagementService.loginCompany(companyLoginPayload);
    }

    @Override
    public List<CompanyResponse> getAllCompanies() {
        return companyRetrievalService.getAllCompanies();
    }

    @Override
    public CompanyResponse getCompaniesById(Long id) {
        return companyRetrievalService.getCompaniesById(id);
    }

    @Override
    public CompanyResponse getCurrentUser() {
        return companyRetrievalService.getCurrentUser();
    }
}
