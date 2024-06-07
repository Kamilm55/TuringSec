package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.model.entities.program.Asset;
import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.payload.program.ProgramPayload;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.service.interfaces.IProgramsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/bug-bounty-programs")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class ProgramController {
    private final IProgramsService bugBountyProgramService;
    @GetMapping
    public BaseResponse<List<Program>> getCompanyAllBugBountyPrograms() {
        // Get programs belonging to the company
        return BaseResponse.success(bugBountyProgramService.getCompanyAllBugBountyPrograms());
    }

    @PostMapping
    public BaseResponse</*BugBountyProgramDTO*/Program> createBugBountyProgram(@RequestBody @Valid ProgramPayload programPayload) {
//          refactorThis: CREATED RESPONSE MESSAGE 201
//           ResponseEntity.created(URI.create("/api/bug-bounty-programs/" + createdOrUpdateProgram.getId())).body(createdOrUpdateProgram);
        return BaseResponse.success(bugBountyProgramService.createBugBountyProgram(programPayload));
    }
    @GetMapping("/{id}/assets") // for report dropdown
    public BaseResponse<Set<Asset>> getAllAssets(@PathVariable Long id) {
        return BaseResponse.success(bugBountyProgramService.getAllAssets(id));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteBugBountyProgram(@PathVariable Long id) {
       bugBountyProgramService.deleteBugBountyProgram(id);
//        refactorThis: NOCONTENT RESPONSE MESSAGE 204
        return  BaseResponse.success(null,"Program deleted successfully");
    }

}