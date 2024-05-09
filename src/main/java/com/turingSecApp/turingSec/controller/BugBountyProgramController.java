package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.response.AssetTypeDTO;
import com.turingSecApp.turingSec.payload.BugBountyProgramWithAssetTypePayload;
import com.turingSecApp.turingSec.response.BugBountyProgramDTO;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.service.interfaces.IProgramsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bug-bounty-programs")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class BugBountyProgramController {
    private final IProgramsService bugBountyProgramService;
    @GetMapping
    public BaseResponse<List<BugBountyProgramDTO>> getCompanyAllBugBountyPrograms() {
        return BaseResponse.success(bugBountyProgramService.getCompanyAllBugBountyPrograms());
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
    public BaseResponse<Void> deleteBugBountyProgram(@PathVariable Long id) {
       bugBountyProgramService.deleteBugBountyProgram(id);
//        refactorThis: NOCONTENT RESPONSE MESSAGE 204
        return  BaseResponse.success(null,"Program deleted successfully");
    }

}