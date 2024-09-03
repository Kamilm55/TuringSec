package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.payload.user.LoginRequest;
import com.turingSecApp.turingSec.response.admin.AdminAuthResponse;

public interface IAdminService {
    AdminAuthResponse loginAdmin(LoginRequest user);
    String approveCompanyRegistration(String companyId);
}
