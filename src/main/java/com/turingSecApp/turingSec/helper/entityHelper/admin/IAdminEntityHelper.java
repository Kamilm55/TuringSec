package com.turingSecApp.turingSec.helper.entityHelper.admin;

import com.turingSecApp.turingSec.model.entities.user.AdminEntity;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.payload.user.LoginRequest;
import com.turingSecApp.turingSec.response.admin.AdminAuthResponse;

public interface IAdminEntityHelper {
    String approveCompanyAndGeneratePass(Long companyId);

    void notifyCompanyForApproval(CompanyEntity company, String generatedPassword);


    AdminEntity getAdminEntity(String usernameOrEmail);

    AdminAuthResponse createAuthResponse(AdminEntity adminEntity);

    CompanyEntity getCompanyById(Long companyId);
}
