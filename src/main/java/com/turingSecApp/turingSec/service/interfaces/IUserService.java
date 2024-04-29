package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.Request.*;
import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
import com.turingSecApp.turingSec.payload.RegisterPayload;
import com.turingSecApp.turingSec.response.AuthResponse;
import com.turingSecApp.turingSec.response.BugBountyProgramDTO;
import com.turingSecApp.turingSec.response.UserHackerDTO;

import java.util.List;

public interface IUserService {
    AuthResponse registerHacker(RegisterPayload registerPayload);
    boolean activateAccount(String token);
    AuthResponse loginUser(LoginRequest loginRequest);
    void changePassword(ChangePasswordRequest request);
    void changeEmail(ChangeEmailRequest request);
    UserHackerDTO updateProfile(UserUpdateRequest userUpdateRequest);
    UserDTO getUserById(Long userId);
    UserDTO getCurrentUser();
    List<UserDTO> getAllUsers();
    void deleteUser();

    List<BugBountyProgramWithAssetTypeDTO> getAllBugBountyPrograms();
    BugBountyProgramDTO getBugBountyProgramById(Long id);
    UserEntity findUserByUsername(String username);

}