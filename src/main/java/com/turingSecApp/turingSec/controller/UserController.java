package com.turingSecApp.turingSec.controller;


import com.turingSecApp.turingSec.Request.*;
import com.turingSecApp.turingSec.background_file_upload_for_hacker.service.FileService;
import com.turingSecApp.turingSec.dao.entities.*;
import com.turingSecApp.turingSec.dao.entities.role.Role;
import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
import com.turingSecApp.turingSec.dao.repository.HackerRepository;
import com.turingSecApp.turingSec.dao.repository.RoleRepository;
import com.turingSecApp.turingSec.dao.repository.UserRepository;
import com.turingSecApp.turingSec.exception.custom.*;
import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.payload.RegisterPayload;
import com.turingSecApp.turingSec.response.AuthResponse;
import com.turingSecApp.turingSec.response.BugBountyProgramDTO;
import com.turingSecApp.turingSec.response.UserHackerDTO;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.service.ProgramsService;
import com.turingSecApp.turingSec.service.user.CustomUserDetails;
import com.turingSecApp.turingSec.service.user.UserService;
import com.turingSecApp.turingSec.util.ProgramMapper;
import com.turingSecApp.turingSec.util.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register/hacker")
     public BaseResponse<AuthResponse> registerHacker(@RequestBody RegisterPayload payload) {
        return BaseResponse.success(userService.registerHacker(payload),
                "You should receive gmail message for account activation");
    }

    @GetMapping("/activate")
    public ResponseEntity<?> activateAccount(@RequestParam("token") String token) {
        boolean activationResult = userService.activateAccount(token);

        if (!activationResult) {
           throw new  InvalidTokenException();
        }
            return ResponseEntity.ok("Account activated successfully! You can login your account.");
    }

    @PostMapping("/login")
    public BaseResponse<AuthResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        return BaseResponse.success(userService.loginUser(loginRequest));
    }

    @PostMapping("/change-password")
    public BaseResponse<?> changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
       return BaseResponse.success(null,"Password updated successfully");
    }

    @PostMapping("/change-email")
    public BaseResponse<?>  changeEmail(@RequestBody ChangeEmailRequest request) {
        userService.changeEmail(request);
        return BaseResponse.success(null,"Email updated successfully");
    }

    @PostMapping("/update-profile")
    public BaseResponse<UserHackerDTO> updateProfile(@RequestBody UserUpdateRequest profileUpdateRequest) {
        UserHackerDTO updateProfile = userService.updateProfile(profileUpdateRequest);

        String newToken = userService.generateNewToken(updateProfile);

        return BaseResponse.success(updateProfile,
                "Profile updated successfully. You must update Authorization header (Bearer token) , new token is: "  + newToken);
    }

    @GetMapping("/users/{userId}")
    public BaseResponse<UserDTO> getUserById(@PathVariable Long userId) {
        return BaseResponse.success(userService.getUserById(userId));
    }

    @GetMapping("/test")
    public String test() {
        return "test passed";
    }

    @GetMapping("/current-user")
    public BaseResponse<UserDTO> getCurrentUser() {
        return BaseResponse.success(userService.getCurrentUser());

    }

    @GetMapping("/allUsers")
    public BaseResponse<List<UserDTO>> getAllUsers() {
        return BaseResponse.success(userService.getAllUsers());
    }

    @DeleteMapping("/delete-user")
    public BaseResponse<?> deleteUser() {
        userService.deleteUser();
        return BaseResponse.success(null,"User deleted successfully. You must delete Authorization header (Bearer token)");
    }

    @GetMapping("/programs")
    public BaseResponse<List<BugBountyProgramWithAssetTypeDTO>> getAllBugBountyPrograms() {
        return BaseResponse.success(userService.getAllBugBountyPrograms());
    }
    @GetMapping("programsById/{id}")
    public BaseResponse<BugBountyProgramDTO> getBugBountyProgramById(@PathVariable Long id) {
        return BaseResponse.success(userService.getBugBountyProgramById(id));
    }

}
