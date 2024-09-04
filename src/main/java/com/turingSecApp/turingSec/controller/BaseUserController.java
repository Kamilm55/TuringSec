package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.model.entities.user.BaseUser;
import com.turingSecApp.turingSec.response.BaseUserDTO;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.service.interfaces.IBaseUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/base-users")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class BaseUserController {
    private final IBaseUserService baseUserService;

    // todo: change to base user dto
    @GetMapping("/current-user")
    public BaseResponse<BaseUserDTO> getCurrentUser() {
        return BaseResponse.success(baseUserService.getCurrentUser());
    }

    @GetMapping("/{baseUserId}")
    public BaseResponse<BaseUserDTO> getUserById(@PathVariable String baseUserId) {
        return BaseResponse.success(baseUserService.getBaseUserById(baseUserId));
    }

    @DeleteMapping("current-user")
    public BaseResponse<?> deleteUser() {
        baseUserService.deleteCurrentBaseUser();
        return BaseResponse.success(null,"User deleted successfully.");
    }

}
