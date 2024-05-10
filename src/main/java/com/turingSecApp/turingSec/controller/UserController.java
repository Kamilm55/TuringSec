package com.turingSecApp.turingSec.controller;


import com.turingSecApp.turingSec.exception.custom.InvalidTokenException;
import com.turingSecApp.turingSec.payload.user.*;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.response.program.BugBountyProgramDTO;
import com.turingSecApp.turingSec.response.program.BugBountyProgramWithAssetTypeDTO;
import com.turingSecApp.turingSec.response.user.AuthResponse;
import com.turingSecApp.turingSec.response.user.UserDTO;
import com.turingSecApp.turingSec.response.user.UserHackerDTO;
import com.turingSecApp.turingSec.service.interfaces.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping("/register/hacker")
     public BaseResponse<AuthResponse> registerHacker(@RequestBody @Valid RegisterPayload payload) {
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
    public BaseResponse<AuthResponse> loginUser(@RequestBody @Valid LoginRequest loginRequest) {
        return BaseResponse.success(userService.loginUser(loginRequest));
    }

    @PostMapping("/change-password")
    public BaseResponse<?> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(request);
       return BaseResponse.success(null,"Password updated successfully");
    }

    @PostMapping("/change-email")
    public BaseResponse<?>  changeEmail(@RequestBody @Valid ChangeEmailRequest request) {
        userService.changeEmail(request);
        return BaseResponse.success(null,"Email updated successfully");
    }

    @PutMapping("/update-profile")
    public BaseResponse<UserHackerDTO> updateProfile(@RequestBody @Valid UserUpdateRequest profileUpdateRequest) {
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
    public BaseResponse<List<UserHackerDTO>> getAllUsers() {
        return BaseResponse.success(userService.getAllActiveUsers());
    }

    @DeleteMapping("/delete-user")
    public BaseResponse<?> deleteUser() {
        userService.deleteUser();
        return BaseResponse.success(null,"User deleted successfully. You must delete Authorization header (Bearer token)");
    }

    // All bug bounty programs for user(hacker)
    @GetMapping("/programs")
    public BaseResponse<List<BugBountyProgramWithAssetTypeDTO>> getAllBugBountyPrograms() {
        return BaseResponse.success(userService.getAllBugBountyPrograms());
    }
    @GetMapping("programsById/{id}")
    public BaseResponse<BugBountyProgramDTO> getBugBountyProgramById(@PathVariable Long id) {
        return BaseResponse.success(userService.getBugBountyProgramById(id));
    }

}
