package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.Request.AssetTypeDTO;
import com.turingSecApp.turingSec.payload.BugBountyProgramWithAssetTypePayload;
import com.turingSecApp.turingSec.response.BugBountyProgramDTO;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.service.interfaces.IProgramsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bug-bounty-programs")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class BugBountyProgramController {
    private final IProgramsService bugBountyProgramService;
    @GetMapping
    public BaseResponse<List<BugBountyProgramDTO>> getAllBugBountyPrograms() {
        return BaseResponse.success(bugBountyProgramService.getAllBugBountyPrograms());
    }

    @PostMapping
    public BaseResponse<BugBountyProgramDTO> createBugBountyProgram( @RequestBody @Valid BugBountyProgramWithAssetTypePayload programDTO) {
//          refactorThis: CREATED RESPONSE MESSAGE 201
//           ResponseEntity.created(URI.create("/api/bug-bounty-programs/" + createdOrUpdateProgram.getId())).body(createdOrUpdateProgram);
        return BaseResponse.success(bugBountyProgramService.createBugBountyProgram(programDTO));
    }
    @GetMapping("/assets")
    public BaseResponse<List<AssetTypeDTO>> getCompanyAssetTypes() {
        return BaseResponse.success(bugBountyProgramService.getCompanyAssetTypes());
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_COMPANY")// refactorThis
    public BaseResponse<Void> deleteBugBountyProgram(@PathVariable Long id) {
       bugBountyProgramService.deleteBugBountyProgram(id);
//        refactorThis: NOCONTENT RESPONSE MESSAGE 204
        return  BaseResponse.success(null,"Program deleted successfully");
    }

}