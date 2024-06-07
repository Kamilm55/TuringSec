package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.payload.user.*;
import com.turingSecApp.turingSec.response.user.AuthResponse;
import com.turingSecApp.turingSec.response.user.UserDTO;
import com.turingSecApp.turingSec.response.user.UserHackerDTO;

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

    // Programs for users
    List<Program> getAllBugBountyPrograms();
    Program getBugBountyProgramById(Long id);
    UserEntity findUserByUsername(String username);

    String generateNewToken(UserHackerDTO updateProfile);
}
