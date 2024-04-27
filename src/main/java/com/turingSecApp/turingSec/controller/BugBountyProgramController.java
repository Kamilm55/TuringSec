package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.Request.AssetTypeDTO;
import com.turingSecApp.turingSec.dao.entities.AssetTypeEntity;
import com.turingSecApp.turingSec.dao.entities.BugBountyProgramEntity;
import com.turingSecApp.turingSec.dao.entities.CompanyEntity;
import com.turingSecApp.turingSec.dao.entities.StrictEntity;
import com.turingSecApp.turingSec.dao.repository.CompanyRepository;
import com.turingSecApp.turingSec.exception.custom.PermissionDeniedException;
import com.turingSecApp.turingSec.exception.custom.UnauthorizedException;
import com.turingSecApp.turingSec.payload.AssetTypePayload;
import com.turingSecApp.turingSec.payload.BugBountyProgramWithAssetTypePayload;
import com.turingSecApp.turingSec.payload.StrictPayload;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.service.AssetTypeService;
import com.turingSecApp.turingSec.service.ProgramsService;
import com.turingSecApp.turingSec.service.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bug-bounty-programs")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class BugBountyProgramController {
    private final ProgramsService bugBountyProgramService;
    private final AssetTypeService assetTypeService;
    private final  CompanyRepository companyRepository;

    @GetMapping
    @Secured("ROLE_COMPANY")
    public BaseResponse<List<BugBountyProgramEntity>> getAllBugBountyPrograms() {
        // Retrieve the email of the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        // Retrieve the company associated with the authenticated user
        CompanyEntity company = companyRepository.findByEmail(userEmail);

        // Check if the company is authenticated
        if (company != null) {
            // Get programs belonging to the company
            List<BugBountyProgramEntity> programs = bugBountyProgramService.getAllBugBountyProgramsByCompany(company);

            return BaseResponse.success(programs);
        } else {
            // Return unauthorized response or handle as needed
            throw new UnauthorizedException();
        }
    }

    @PostMapping
    public BaseResponse<BugBountyProgramEntity> createBugBountyProgram(@Valid @RequestBody BugBountyProgramWithAssetTypePayload programDTO) {
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
        BugBountyProgramEntity createdOrUpdateProgram = bugBountyProgramService.createOrUpdateBugBountyProgram(program);
//          refactorThis: CREATED RESPONSE MESSAGE 201
//           ResponseEntity.created(URI.create("/api/bug-bounty-programs/" + createdOrUpdateProgram.getId())).body(createdOrUpdateProgram);
        return BaseResponse.success(createdOrUpdateProgram);
    }
    @GetMapping("/assets")
    public BaseResponse<List<AssetTypeDTO>> getCompanyAssetTypes() {
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
            List<AssetTypeDTO> assetTypeDTOs = assetTypeEntities.stream()
                    .map(assetTypeEntity -> {
                        AssetTypeDTO dto = mapToDTO(assetTypeEntity);
                        dto.setProgramId(assetTypeEntity.getBugBountyProgram().getId());
                        return dto;
                    })
                    .collect(Collectors.toList());

            return BaseResponse.success(assetTypeDTOs);
        } else {
            // Return unauthorized response or handle as needed
           throw new UnauthorizedException();
        }
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_COMPANY")
    public BaseResponse<Void> deleteBugBountyProgram(@PathVariable Long id) {
        // Get the authenticated user details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Get the company associated with the authenticated user
        CompanyEntity company = (CompanyEntity) userDetails.getUser();

        // Retrieve the bug bounty program by ID
        BugBountyProgramEntity program = bugBountyProgramService.getBugBountyProgramById(id);

        // Check if the authenticated company is the owner of the program
        if (program.getCompany().getId().equals(company.getId())) {
            // Proceed with deleting the bug bounty program
            bugBountyProgramService.deleteBugBountyProgram(id);
            //          refactorThis: NOCONTENT RESPONSE MESSAGE 204
            return BaseResponse.success(null,"Program deleted successfully");
        } else {
            // If the authenticated company is not the owner, return forbidden status
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            throw new PermissionDeniedException();
        }
    }

    // Util methods
    private AssetTypeDTO mapToDTO(AssetTypeEntity assetTypeEntity) {
        AssetTypeDTO dto = new AssetTypeDTO();
//        dto.setId(assetTypeEntity.getId());
        dto.setLevel(assetTypeEntity.getLevel());
        dto.setAssetType(assetTypeEntity.getAssetType());
        dto.setPrice(assetTypeEntity.getPrice());
        return dto;
    }
}