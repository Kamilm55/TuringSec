package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
import com.turingSecApp.turingSec.payload.*;
import com.turingSecApp.turingSec.response.*;

import java.util.List;

public interface IUserService {
    AuthResponse registerHacker(RegisterPayload registerPayload);
    void insertActiveHacker(RegisterPayload registerPayload); // for testing
    boolean activateAccount(String token);
    AuthResponse loginUser(LoginRequest loginRequest);
    void changePassword(ChangePasswordRequest request);
    void changeEmail(ChangeEmailRequest request);
    UserHackerDTO updateProfile(UserUpdateRequest userUpdateRequest);
    UserDTO getUserById(Long userId);
    UserDTO getCurrentUser();
    List<UserHackerDTO> getAllActiveUsers();
    void deleteUser();

    List<BugBountyProgramWithAssetTypeDTO> getAllBugBountyPrograms();
    BugBountyProgramDTO getBugBountyProgramById(Long id);
    UserEntity findUserByUsername(String username);

    String generateNewToken(UserHackerDTO updateProfile);
}
