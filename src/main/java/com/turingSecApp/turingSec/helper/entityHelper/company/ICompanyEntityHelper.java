package com.turingSecApp.turingSec.helper.entityHelper.company;

import com.turingSecApp.turingSec.model.entities.role.Role;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.payload.company.RegisterCompanyPayload;

import java.util.Map;

public interface ICompanyEntityHelper {
    void checkCompanyEmailUnique(String email);

    Role findCompanyRole();

    CompanyEntity buildCompanyEntity(RegisterCompanyPayload companyPayload, Role companyRole);

    void notifyAdminsForApproval(CompanyEntity savedCompany);

    Map<String, String> createResponse(CompanyEntity companyEntity);
}
