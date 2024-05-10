package com.turingSecApp.turingSec.util;

import com.turingSecApp.turingSec.dao.entities.AssetTypeEntity;
import com.turingSecApp.turingSec.dao.entities.BugBountyProgramEntity;
import com.turingSecApp.turingSec.dao.entities.CompanyEntity;
import com.turingSecApp.turingSec.dao.entities.HackerEntity;
import com.turingSecApp.turingSec.dao.entities.role.Role;
import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
import com.turingSecApp.turingSec.dao.repository.CompanyRepository;
import com.turingSecApp.turingSec.dao.repository.RoleRepository;
import com.turingSecApp.turingSec.dao.repository.UserRepository;
import com.turingSecApp.turingSec.exception.custom.*;
import com.turingSecApp.turingSec.response.program.AssetTypeDTO;
import com.turingSecApp.turingSec.response.program.BugBountyProgramWithAssetTypeDTO;
import com.turingSecApp.turingSec.response.user.AuthResponse;
import com.turingSecApp.turingSec.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UtilService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    // Company service
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

    public BugBountyProgramWithAssetTypeDTO mapToDTO(BugBountyProgramEntity programEntity) {
        BugBountyProgramWithAssetTypeDTO dto = new BugBountyProgramWithAssetTypeDTO();
//        dto.setId(programEntity.getId());
        dto.setFromDate(programEntity.getFromDate());
        dto.setToDate(programEntity.getToDate());
        dto.setNotes(programEntity.getNotes());
        dto.setPolicy(programEntity.getPolicy());

        // Map associated asset types
        List<AssetTypeDTO> assetTypeDTOs = programEntity.getAssetTypes().stream()
                .map(this::mapAssetTypeToDTO)
                .collect(Collectors.toList());
        dto.setAssetTypes(assetTypeDTOs);

        // You can map other fields as needed

        return dto;
    }
    public AssetTypeDTO mapAssetTypeToDTO(AssetTypeEntity assetTypeEntity) {
        AssetTypeDTO dto = new AssetTypeDTO();
//        dto.setId(assetTypeEntity.getId());
        dto.setLevel(assetTypeEntity.getLevel());
        dto.setAssetType(assetTypeEntity.getAssetType());
        dto.setPrice(assetTypeEntity.getPrice());
        dto.setProgramId(assetTypeEntity.getBugBountyProgram().getId());

        return dto;
    }

}
