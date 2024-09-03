package com.turingSecApp.turingSec.service.user;


import com.turingSecApp.turingSec.payload.user.*;
import com.turingSecApp.turingSec.response.program.ProgramDTO;
import com.turingSecApp.turingSec.response.user.AuthResponse;
import com.turingSecApp.turingSec.response.user.UserDTO;
import com.turingSecApp.turingSec.response.user.UserHackerDTO;
import com.turingSecApp.turingSec.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserManagementService userManagementService;
    private final UserRetrievalService userRetrievalService;
    @Override
    public AuthResponse registerHacker(RegisterPayload registerPayload) {
        return userManagementService.registerHacker(registerPayload);
    }

    @Override
    @Transactional // A collection with cascade="all-delete-orphan" was no longer referenced by the owning entity instance: com.turingSecApp.turingSec.dao.entities.user.UserEntity.reports
    public void insertActiveHacker(RegisterPayload registerPayload) {
        userManagementService.insertActiveHacker(registerPayload);
    }
    @Override
    public boolean activateAccount(String token) {
        return userRetrievalService.activateAccount(token);
    }

    @Override
    public AuthResponse loginUser(LoginRequest loginRequest) {
        return userManagementService.loginUser(loginRequest);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        userManagementService.changePassword(request);
    }


    @Override
    public void changeEmail(ChangeEmailRequest request) {
        userManagementService.changeEmail(request);
    }

    @Override
    public UserHackerDTO updateProfile(UserUpdateRequest userUpdateRequest) {
        return userManagementService.updateProfile(userUpdateRequest);
    }

    @Override
    public UserDTO getUserById(String userId) {
        return userRetrievalService.getUserById(userId);
    }

    @Override
    public UserDTO getCurrentUser() {
        return userRetrievalService.getCurrentUser();
    }

    @Override
    public List<UserHackerDTO> getAllActiveUsers() {
        return userRetrievalService.getAllActiveUsers();
    }

    @Override
    public List<ProgramDTO> getAllBugBountyPrograms() {
        return userRetrievalService.getAllBugBountyPrograms();
    }


    @Override
    public ProgramDTO getBugBountyProgramById(Long id) {
        return userRetrievalService.getBugBountyProgramById(id);
    }


}

