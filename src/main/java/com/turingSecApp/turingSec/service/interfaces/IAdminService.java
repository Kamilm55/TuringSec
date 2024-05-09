package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.payload.LoginRequest;
import com.turingSecApp.turingSec.response.AdminAuthResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

public interface IAdminService {
    AdminAuthResponse loginAdmin(LoginRequest user);
    String approveCompanyRegistration(Long companyId);
}
