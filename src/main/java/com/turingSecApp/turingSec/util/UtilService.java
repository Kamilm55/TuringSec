package com.turingSecApp.turingSec.util;

import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.model.entities.role.Role;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.entities.user.HackerEntity;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.model.repository.CompanyRepository;
import com.turingSecApp.turingSec.model.repository.RoleRepository;
import com.turingSecApp.turingSec.model.repository.UserRepository;
import com.turingSecApp.turingSec.exception.custom.*;
import com.turingSecApp.turingSec.model.repository.program.ProgramRepository;
import com.turingSecApp.turingSec.response.program.BugBountyProgramWithAssetTypeDTO;
import com.turingSecApp.turingSec.response.user.AuthResponse;
import com.turingSecApp.turingSec.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UtilService {
    private final CompanyRepository companyRepository;
    private final ProgramRepository programRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    // Method to retrieve authenticated user(Hacker)
    public UserEntity getAuthenticatedHacker() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));
        } else {
            throw new UnauthorizedException();
        }
    }

    // Method to retrieve authenticated company
    public CompanyEntity getAuthenticatedCompany() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            CompanyEntity company = companyRepository.findByEmail(email);
            if(company==null){
                throw  new CompanyNotFoundException("Company with email " + email + " not found , in getAuthenticatedCompany()");
            }
            return company;
        } else {
            throw new UnauthorizedException();
        }
    }

    // User service
    // Method to get hacker roles
    public Set<Role> getHackerRoles() {
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName("HACKER"));
        return roles;
    }
    public Set<Role> getAdminRoles() {
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName("ADMIN"));
        return roles;
    }

    // Method to build authentication response
    public AuthResponse buildAuthResponse(String token, UserEntity user, HackerEntity hacker) {
        return AuthResponse.builder()
                .accessToken(token)
                .userInfo(UserMapper.INSTANCE.toDto(user, hacker))
                .build();
    }
    public void isUserExistWithEmail(String email) {
//        System.out.println(email);
        if (userRepository.findByEmail(email) != null) {
            throw new EmailAlreadyExistsException("Email is already taken.");
        }
    }

    public void isUserExistWithUsername(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("Username is already taken.");
        }
    }
    // Method to check if the user is activated
    public void checkUserIsActivated(UserEntity userEntity) {
        if (!userEntity.isActivated()) {
            throw new UserNotActivatedException("User is not activated yet.");
        }
    }

    public BugBountyProgramWithAssetTypeDTO mapToDTO(Program programEntity) {
        BugBountyProgramWithAssetTypeDTO dto = new BugBountyProgramWithAssetTypeDTO();
//        dto.setId(programEntity.getId());
        dto.setFromDate(programEntity.getFromDate());
        dto.setToDate(programEntity.getToDate());
        dto.setNotes(programEntity.getNotes());
        dto.setPolicy(programEntity.getPolicy());

//        dto.setAssets(programEntity.getAsset());

        // You can map other fields as needed

        return dto;
    }

    // Media services
    public Long validateHacker(UserDetails userDetails) {
        validateUserDetails(userDetails);
        UserEntity userEntity = getUserEntity(userDetails);
        return getHackerId(userEntity);
    }
    private void validateUserDetails(UserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedException();
        }
    }
    private UserEntity getUserEntity(UserDetails userDetails) {
        String username = userDetails.getUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));
    }
    private Long getHackerId(UserEntity userEntity) {
        HackerEntity hackerEntity = userEntity.getHacker();
        if (hackerEntity == null) {
            throw new UserNotFoundException("Hacker ID not found for the authenticated user!");
        }
        return hackerEntity.getId();
    }
    public Program findProgramById(Long id) {
        return programRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bug Bounty Program not found with id:" + id));
    }
}
