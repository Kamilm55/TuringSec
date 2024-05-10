package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.payload.user.LoginRequest;
import com.turingSecApp.turingSec.response.admin.AdminAuthResponse;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.service.interfaces.IAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class AdminController {
    private final IAdminService adminService;

    @PostMapping("/approve-company/{companyId}")
    public BaseResponse<?> approveCompanyRegistration(@PathVariable Long companyId) {
        return BaseResponse.success("Company registration approved successfully. Generated password: " + adminService.approveCompanyRegistration(companyId));
    }


    @PostMapping("/login")
    public BaseResponse<AdminAuthResponse> loginAdmin(@RequestBody @Valid LoginRequest user) {
       return BaseResponse.success(adminService.loginAdmin(user));
    }

}
