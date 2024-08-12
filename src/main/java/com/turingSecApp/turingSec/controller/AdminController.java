package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.model.entities.user.AdminEntity;
import com.turingSecApp.turingSec.payload.user.AdminUpdateRequest;
import com.turingSecApp.turingSec.payload.user.LoginRequest;
import com.turingSecApp.turingSec.response.admin.AdminAuthResponse;
import com.turingSecApp.turingSec.response.admin.AdminDTO;
import com.turingSecApp.turingSec.response.admin.AdminUpdateResponse;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.response.company.CompanyResponse;
import com.turingSecApp.turingSec.service.interfaces.IAdminService;
import com.turingSecApp.turingSec.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class AdminController {
    private final IAdminService adminService;
    private final UserService userService;

    @PostMapping("/approve-company/{companyId}")
    public BaseResponse<?> approveCompanyRegistration(@PathVariable Long companyId) {
        return BaseResponse.success("Company registration approved successfully. Generated password: " + adminService.approveCompanyRegistration(companyId));
    }


    @PostMapping("/login")
    public BaseResponse<AdminAuthResponse> loginAdmin(@RequestBody @Valid LoginRequest user) {
       return BaseResponse.success(adminService.loginAdmin(user));
    }

    @PutMapping(path = "/update")
    public BaseResponse<AdminDTO> adminUpdate(@Valid @RequestBody AdminUpdateRequest adminUpdateRequest){
        AdminDTO adminEntity = adminService.updateAdmin(adminUpdateRequest);
        String newToken = userService.generateAdminNewToken(adminUpdateRequest.getUsername());
        return BaseResponse.success(adminEntity,
                "Admin updated successfully. You must update Authorization header (Bearer token) , new token is: "
                        + newToken
        );
    }

}
