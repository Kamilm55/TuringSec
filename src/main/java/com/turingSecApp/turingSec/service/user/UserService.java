package com.turingSecApp.turingSec.service.user;


import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.entities.user.HackerEntity;
import com.turingSecApp.turingSec.model.entities.role.Role;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.model.repository.*;
import com.turingSecApp.turingSec.model.repository.program.ProgramRepository;
import com.turingSecApp.turingSec.exception.custom.BadCredentialsException;
import com.turingSecApp.turingSec.exception.custom.EmailAlreadyExistsException;
import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.payload.user.*;
import com.turingSecApp.turingSec.response.program.ProgramDTO;
import com.turingSecApp.turingSec.response.user.AuthResponse;
import com.turingSecApp.turingSec.response.user.UserDTO;
import com.turingSecApp.turingSec.response.user.UserHackerDTO;
import com.turingSecApp.turingSec.service.EmailNotificationService;
import com.turingSecApp.turingSec.service.program.ProgramService;
import com.turingSecApp.turingSec.service.interfaces.IUserService;
import com.turingSecApp.turingSec.util.GlobalConstants;
import com.turingSecApp.turingSec.util.UtilService;
import com.turingSecApp.turingSec.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


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
    public String generateNewToken(UserHackerDTO updatedUser) {
        return userManagementService.generateNewToken(updatedUser);
    }

    @Override
    public UserDTO getUserById(Long userId) {
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
    @Transactional// This annotation ensures that the method is executed within a transactional context, allowing database operations like deletion to be performed reliably.
    public void deleteUser() {
        userManagementService.deleteUser();
    }

    @Override
    public ProgramDTO getBugBountyProgramById(Long id) {
        return userRetrievalService.getBugBountyProgramById(id);
    }


}

