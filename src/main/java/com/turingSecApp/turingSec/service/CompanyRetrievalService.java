package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.helper.entityHelper.company.ICompanyEntityHelper;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.repository.CompanyRepository;
import com.turingSecApp.turingSec.response.company.CompanyResponse;
import com.turingSecApp.turingSec.util.UtilService;
import com.turingSecApp.turingSec.util.mapper.CompanyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyRetrievalService {
    private final CompanyRepository companyRepository;
    private final ICompanyEntityHelper companyEntityHelper;
    private final UtilService utilService;


    public List<CompanyResponse> getAllCompanies() {
        List<CompanyEntity> companyEntities = companyRepository.findAll();
        return companyEntities.stream().map(CompanyMapper.INSTANCE::convertToResponse).collect(Collectors.toList());
    }

    public CompanyResponse getCompaniesById(Long id) {
        CompanyEntity company = findCompanyById(id);
        return CompanyMapper.INSTANCE.convertToResponse(company);
    }

    public CompanyResponse getCurrentUser() {
        // Retrieve user details from the database
        CompanyEntity company = utilService.getAuthenticatedCompany();
        return CompanyMapper.INSTANCE.convertToResponse(company);
    }

    private CompanyEntity findCompanyById(Long id) {
        Optional<CompanyEntity> companyEntity = companyRepository.findById(id);
        return companyEntity.orElseThrow(() -> new ResourceNotFoundException("Company not found with id:" + id));
    }
}
