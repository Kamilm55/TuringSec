package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.Request.AssetTypeDTO;
import com.turingSecApp.turingSec.dao.entities.AssetTypeEntity;
import com.turingSecApp.turingSec.dao.entities.BugBountyProgramEntity;
import com.turingSecApp.turingSec.dao.entities.CompanyEntity;
import com.turingSecApp.turingSec.dao.entities.StrictEntity;
import com.turingSecApp.turingSec.dao.repository.CompanyRepository;
import com.turingSecApp.turingSec.dao.repository.ProgramsRepository;
import com.turingSecApp.turingSec.exception.custom.PermissionDeniedException;
import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.exception.custom.UnauthorizedException;
import com.turingSecApp.turingSec.payload.AssetTypePayload;
import com.turingSecApp.turingSec.payload.BugBountyProgramWithAssetTypePayload;
import com.turingSecApp.turingSec.payload.StrictPayload;
import com.turingSecApp.turingSec.response.BugBountyProgramDTO;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.service.interfaces.IProgramsService;
import com.turingSecApp.turingSec.service.user.CustomUserDetails;
import com.turingSecApp.turingSec.util.ProgramMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramsService implements IProgramsService {

    private final AssetTypeService assetTypeService;
    private final ProgramsRepository programsRepository;

    private final CompanyRepository companyRepository;

    @Override
    public List<BugBountyProgramDTO> getAllBugBountyPrograms(){
        //   Retrieve the email of the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        // Retrieve the company associated with the authenticated user
        CompanyEntity company = companyRepository.findByEmail(userEmail);

        // Check if the company is authenticated
        if (company != null) {
            // Get programs belonging to the company
            List<BugBountyProgramEntity> programs = programsRepository.findByCompany(company);

            return programs.stream().map(ProgramMapper.INSTANCE::toDto).collect(Collectors.toList());
        } else {
            // Return unauthorized response or handle as needed
            throw new UnauthorizedException();
        }
    }

    @Override
    public BugBountyProgramDTO createBugBountyProgram(BugBountyProgramWithAssetTypePayload programDTO) {
        // Get the authenticated user details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Extract the company from the authenticated user details
        CompanyEntity company = (CompanyEntity) userDetails.getUser();

        // Convert DTO to entity
        BugBountyProgramEntity program = new BugBountyProgramEntity();
        program.setFromDate(programDTO.getFromDate());
        program.setToDate(programDTO.getToDate());
        program.setNotes(programDTO.getNotes());
        program.setPolicy(programDTO.getPolicy());
        program.setCompany(company);

        // Convert AssetTypeDTOs to AssetTypeEntities
        List<AssetTypePayload> assetTypeDTOs = programDTO.getAssetTypes();
        List<AssetTypeEntity> assetTypes = assetTypeDTOs.stream()
                .map(assetTypeDTO -> {
                    AssetTypeEntity assetTypeEntity = new AssetTypeEntity();
                    assetTypeEntity.setLevel(assetTypeDTO.getLevel());
                    assetTypeEntity.setAssetType(assetTypeDTO.getAssetType());
                    assetTypeEntity.setPrice(assetTypeDTO.getPrice());
                    assetTypeEntity.setBugBountyProgram(program);
                    return assetTypeEntity;
                })
                .collect(Collectors.toList());

        // Set the list of asset types for the program
        program.setAssetTypes(assetTypes);

        // Convert ProhibitsDTOs to StrictEntities
        List<StrictPayload> prohibitsDTOs = programDTO.getProhibits();
        List<StrictEntity> prohibits = prohibitsDTOs.stream()
                .map(prohibitDTO -> {
                    StrictEntity strictEntity = new StrictEntity();
                    strictEntity.setProhibitAdded(prohibitDTO.getProhibitAdded());
                    strictEntity.setBugBountyProgramForStrict(program);
                    return strictEntity;
                })
                .collect(Collectors.toList());

        // Set the list of prohibits for the program
        program.setProhibits(prohibits);


        // Proceed with creating or updating the bug bounty program
        BugBountyProgramEntity createdOrUpdateProgram = createOrUpdateBugBountyProgram(program);

        return ProgramMapper.INSTANCE.toDto(createdOrUpdateProgram);
    }
    private BugBountyProgramEntity createOrUpdateBugBountyProgram(BugBountyProgramEntity program) {
        // Check if a program with the same parameters already exists for the company
        List<BugBountyProgramEntity> programs = programsRepository.findByCompany(program.getCompany());
        if (!programs.isEmpty()) {
            BugBountyProgramEntity existingProgram = programs.get(0);
            // Update existing program with the new data
            updateProgramFields(existingProgram, program);
            return programsRepository.save(existingProgram);
        } else {
            // Create new program
            return programsRepository.save(program);
        }

    }
    private void updateProgramFields(BugBountyProgramEntity existingProgram, BugBountyProgramEntity newProgram) {
        // Update fields of the existing program with the new program data
        existingProgram.setFromDate(newProgram.getFromDate());
        existingProgram.setToDate(newProgram.getToDate());
        existingProgram.setNotes(newProgram.getNotes());
        existingProgram.setPolicy(newProgram.getPolicy());

        // Update or add new asset types (if needed)
        List<AssetTypeEntity> existingAssetTypes = existingProgram.getAssetTypes();
        List<AssetTypeEntity> updatedAssetTypes = new ArrayList<>();

        // Update or add new strict  (if needed)
        List<StrictEntity> existingProgramProhibits = existingProgram.getProhibits();
        List<StrictEntity> updateProhibits = new ArrayList<>();

        for (StrictEntity strictEntity : newProgram.getProhibits()) {
            StrictEntity existingProhibit = findExistingProhibits(existingProgramProhibits, strictEntity);
            if (existingProhibit != null) {
                // Update existing asset type
                existingProhibit.setProhibitAdded(strictEntity.getProhibitAdded());

                updateProhibits.add(existingProhibit);
            } else {
                // Add new asset type
                strictEntity.setBugBountyProgramForStrict(existingProgram);
                updateProhibits.add(strictEntity);
            }
        }

        existingProgram.getProhibits().clear();
        existingProgram.getProhibits().addAll(updateProhibits);

//////////////////////////////////////////
        for (AssetTypeEntity assetType : newProgram.getAssetTypes()) {
            AssetTypeEntity existingAssetType = findExistingAssetType(existingAssetTypes, assetType);
            if (existingAssetType != null) {
                // Update existing asset type
                existingAssetType.setLevel(assetType.getLevel());
                existingAssetType.setAssetType(assetType.getAssetType());
                existingAssetType.setPrice(assetType.getPrice());
                updatedAssetTypes.add(existingAssetType);
            } else {
                // Add new asset type
                assetType.setBugBountyProgram(existingProgram);
                updatedAssetTypes.add(assetType);
            }
        }

        existingProgram.getAssetTypes().clear();
        existingProgram.getAssetTypes().addAll(updatedAssetTypes);
    }


    @Override
    public List<AssetTypeDTO> getCompanyAssetTypes() {
        // Retrieve the email of the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        // Retrieve the company associated with the authenticated user
        CompanyEntity company = companyRepository.findByEmail(userEmail);

        // Check if the company is authenticated
        if (company != null) {
            // Get assets belonging to the company
            List<AssetTypeEntity> assetTypeEntities = assetTypeService.getCompanyAssetTypes(company);

            // Map AssetTypeEntities to AssetTypeDTOs

            return assetTypeEntities.stream()
                    .map(assetTypeEntity -> {
                        AssetTypeDTO dto = mapToDTO(assetTypeEntity);
                        dto.setProgramId(assetTypeEntity.getBugBountyProgram().getId());
                        return dto;
                    })
                    .collect(Collectors.toList());
        } else {
            // Return unauthorized response or handle as needed
            throw new UnauthorizedException();
        }
    }

    //
    private AssetTypeDTO mapToDTO(AssetTypeEntity assetTypeEntity) {
        AssetTypeDTO dto = new AssetTypeDTO();
//        dto.setId(assetTypeEntity.getId());
        dto.setLevel(assetTypeEntity.getLevel());
        dto.setAssetType(assetTypeEntity.getAssetType());
        dto.setPrice(assetTypeEntity.getPrice());
        return dto;
    }

    @Override
    public void deleteBugBountyProgram(Long id){
        // Get the authenticated user details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Get the company associated with the authenticated user
        CompanyEntity company = (CompanyEntity) userDetails.getUser();

        // Retrieve the bug bounty program by ID
        BugBountyProgramEntity program = getBugBountyProgramById(id);

        // Check if the authenticated company is the owner of the program
        if (program.getCompany().getId().equals(company.getId())) {
            programsRepository.delete(program);

        } else {
            // If the authenticated company is not the owner, return forbidden status
            throw new PermissionDeniedException();
        }
    }



    // Utils
    private AssetTypeEntity findExistingAssetType(List<AssetTypeEntity> existingAssetTypes, AssetTypeEntity assetType) {
        for (AssetTypeEntity existing : existingAssetTypes) {
            if (existing.getId().equals(assetType.getId())) {
                return existing;
            }
        }
        return null;
    }

    private StrictEntity findExistingProhibits(List<StrictEntity> existingProhibits, StrictEntity strictEntity) {
        for (StrictEntity existing : existingProhibits) {
            if (existing.getId().equals(strictEntity.getId())) {
                return existing;
            }
        }
        return null;
    }


    // Related to UserService
    public List<BugBountyProgramEntity> getAllBugBountyProgramsAsEntity() {
        return programsRepository.findAll();
    }


    public BugBountyProgramEntity getBugBountyProgramById(Long id) {
        Optional<BugBountyProgramEntity> program = programsRepository.findById(id);


        return program.orElseThrow(() -> new ResourceNotFoundException("Bug Bounty Program not found"));
    }
    //

}
