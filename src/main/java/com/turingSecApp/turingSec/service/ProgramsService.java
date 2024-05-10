package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.exception.custom.CompanyNotFoundException;
import com.turingSecApp.turingSec.response.program.AssetTypeDTO;
import com.turingSecApp.turingSec.dao.entities.AssetTypeEntity;
import com.turingSecApp.turingSec.dao.entities.BugBountyProgramEntity;
import com.turingSecApp.turingSec.dao.entities.CompanyEntity;
import com.turingSecApp.turingSec.dao.entities.StrictEntity;
import com.turingSecApp.turingSec.dao.repository.CompanyRepository;
import com.turingSecApp.turingSec.dao.repository.ProgramsRepository;
import com.turingSecApp.turingSec.exception.custom.PermissionDeniedException;
import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.payload.program.AssetTypePayload;
import com.turingSecApp.turingSec.payload.program.BugBountyProgramWithAssetTypePayload;
import com.turingSecApp.turingSec.payload.program.StrictPayload;
import com.turingSecApp.turingSec.response.program.BugBountyProgramDTO;
import com.turingSecApp.turingSec.service.interfaces.IProgramsService;
import com.turingSecApp.turingSec.util.UtilService;
import com.turingSecApp.turingSec.util.mapper.ProgramMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramsService implements IProgramsService {

    private final AssetTypeService assetTypeService;
    private final ProgramsRepository programsRepository;
    private final UtilService utilService;

    private final CompanyRepository companyRepository;

    @Override
    public List<BugBountyProgramDTO> getCompanyAllBugBountyPrograms(){
        // Retrieve the company associated with the authenticated user
        CompanyEntity company = utilService.getAuthenticatedCompany();

        // Get programs belonging to the company
        List<BugBountyProgramEntity> programs = programsRepository.findByCompany(company);

        return programs.stream().map(ProgramMapper.INSTANCE::toDto).collect(Collectors.toList());
    }


    @Override
    @Transactional
    public BugBountyProgramDTO createBugBountyProgram(BugBountyProgramWithAssetTypePayload programDTO) {
        CompanyEntity company = utilService.getAuthenticatedCompany();
        BugBountyProgramEntity program = convertToBugBountyProgramEntity(programDTO, company);
        BugBountyProgramEntity createdOrUpdateProgram = createOrUpdateBugBountyProgram(program);
        return ProgramMapper.INSTANCE.toDto(createdOrUpdateProgram);
    }
    @Transactional // For commandlineRunner (mock data)
    public void createBugBountyProgramForTest(BugBountyProgramWithAssetTypePayload programDTO , CompanyEntity company) {
//        CompanyEntity company = getAuthenticatedUser();
        BugBountyProgramEntity program = convertToBugBountyProgramEntity(programDTO, company);

        BugBountyProgramEntity createdOrUpdateProgram = createOrUpdateBugBountyProgram(program);
        ProgramMapper.INSTANCE.toDto(createdOrUpdateProgram);
    }

    private BugBountyProgramEntity convertToBugBountyProgramEntity(BugBountyProgramWithAssetTypePayload programDTO, CompanyEntity company) {
        BugBountyProgramEntity program = new BugBountyProgramEntity();
        program.setFromDate(programDTO.getFromDate());
        program.setToDate(programDTO.getToDate());
        program.setNotes(programDTO.getNotes());
        program.setPolicy(programDTO.getPolicy());
        program.setCompany(company);
        program.setInScope(programDTO.getInScope());
        program.setOutOfScope(programDTO.getOutOfScope());
        program.setAssetTypes(convertToAssetTypeEntities(programDTO.getAssetTypes(), program));
        program.setProhibits(convertToStrictEntities(programDTO.getProhibits(), program));
        return program;
    }

    private List<AssetTypeEntity> convertToAssetTypeEntities(List<AssetTypePayload> assetTypeDTOs, BugBountyProgramEntity program) {
        return assetTypeDTOs.stream()
                .map(assetTypeDTO -> {
                    AssetTypeEntity assetTypeEntity = new AssetTypeEntity();
                    assetTypeEntity.setLevel(assetTypeDTO.getLevel());
                    assetTypeEntity.setAssetType(assetTypeDTO.getAssetType());
                    assetTypeEntity.setPrice(assetTypeDTO.getPrice());
                    assetTypeEntity.setBugBountyProgram(program);
                    return assetTypeEntity;
                })
                .collect(Collectors.toList());
    }

    private List<StrictEntity> convertToStrictEntities(List<StrictPayload> prohibitsDTOs, BugBountyProgramEntity program) {
        return prohibitsDTOs.stream()
                .map(prohibitDTO -> {
                    StrictEntity strictEntity = new StrictEntity();
                    strictEntity.setProhibitAdded(prohibitDTO.getProhibitAdded());
                    strictEntity.setBugBountyProgramForStrict(program);
                    return strictEntity;
                })
                .collect(Collectors.toList());
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
        updateProgramDates(existingProgram, newProgram);
        existingProgram.setNotes(newProgram.getNotes());
        existingProgram.setPolicy(newProgram.getPolicy());
        updateProgramScope(existingProgram, newProgram);
        updateProgramAssetTypes(existingProgram, newProgram);
        updateProgramProhibits(existingProgram, newProgram);
    }

    private void updateProgramDates(BugBountyProgramEntity existingProgram, BugBountyProgramEntity newProgram) {
        existingProgram.setFromDate(newProgram.getFromDate());
        existingProgram.setToDate(newProgram.getToDate());
    }

    private void updateProgramScope(BugBountyProgramEntity existingProgram, BugBountyProgramEntity newProgram) {
        existingProgram.setInScope(newProgram.getInScope());
        existingProgram.setOutOfScope(newProgram.getOutOfScope());
    }

    private void updateProgramAssetTypes(BugBountyProgramEntity existingProgram, BugBountyProgramEntity newProgram) {
        List<AssetTypeEntity> updatedAssetTypes = new ArrayList<>();
        for (AssetTypeEntity assetType : newProgram.getAssetTypes()) {
            AssetTypeEntity existingAssetType = findExistingAssetType(existingProgram.getAssetTypes(), assetType);
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

    private void updateProgramProhibits(BugBountyProgramEntity existingProgram, BugBountyProgramEntity newProgram) {
        List<StrictEntity> updatedProhibits = new ArrayList<>();
        for (StrictEntity strictEntity : newProgram.getProhibits()) {
            StrictEntity existingProhibit = findExistingProhibits(existingProgram.getProhibits(), strictEntity);
            if (existingProhibit != null) {
                // Update existing prohibit
                existingProhibit.setProhibitAdded(strictEntity.getProhibitAdded());
                updatedProhibits.add(existingProhibit);
            } else {
                // Add new prohibit
                strictEntity.setBugBountyProgramForStrict(existingProgram);
                updatedProhibits.add(strictEntity);
            }
        }
        existingProgram.getProhibits().clear();
        existingProgram.getProhibits().addAll(updatedProhibits);
    }


    @Override
    public List<AssetTypeDTO> getCompanyAssetTypes() {
       // Retrieve the company associated with the authenticated user
        CompanyEntity company = utilService.getAuthenticatedCompany();

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
    @Transactional
    public void deleteBugBountyProgram(Long id){
        // Get the company associated with the authenticated user
        CompanyEntity company = utilService.getAuthenticatedCompany();

        // Retrieve the bug bounty program by ID
        BugBountyProgramEntity program = getBugBountyProgramById(id);

        // Check if the authenticated company is the owner of the program
        if (program.getCompany().getId().equals(company.getId())) {
            company.removeProgram(program.getId());// without this not work , we remove program from set in company entity then we can delete
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
        return programsRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bug Bounty Program not found"));
    }
    //

}
