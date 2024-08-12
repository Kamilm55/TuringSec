package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.model.entities.user.AdminEntity;
import com.turingSecApp.turingSec.payload.user.AdminUpdateRequest;
import com.turingSecApp.turingSec.payload.user.LoginRequest;
import com.turingSecApp.turingSec.response.admin.AdminAuthResponse;
import com.turingSecApp.turingSec.response.admin.AdminDTO;
import com.turingSecApp.turingSec.response.admin.AdminUpdateResponse;
import com.turingSecApp.turingSec.response.company.CompanyResponse;

public interface IAdminService {
    AdminAuthResponse loginAdmin(LoginRequest user);
    String approveCompanyRegistration(Long companyId);

    AdminDTO updateAdmin(AdminUpdateRequest adminUpdateRequest);
}
