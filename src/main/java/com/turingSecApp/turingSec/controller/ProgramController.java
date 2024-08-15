package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.model.entities.program.Asset;
import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.payload.program.ProgramPayload;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.response.program.ProgramDTO;
import com.turingSecApp.turingSec.service.interfaces.IProgramsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/bug-bounty-programs")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class ProgramController {
    private final IProgramsService bugBountyProgramService;
    @GetMapping
    public BaseResponse<List<ProgramDTO>> getCompanyAllBugBountyPrograms() {
        // Get programs belonging to the company
        return BaseResponse.success(bugBountyProgramService.getCompanyAllBugBountyPrograms());
    }

    @PostMapping
    public ResponseEntity<BaseResponse<ProgramDTO>> createBugBountyProgram(@RequestBody @Valid ProgramPayload programPayload) {
        ProgramDTO programDTO = bugBountyProgramService.createBugBountyProgram(programPayload);
        URI uri = URI.create("/api/bug-bounty-programs/" + programDTO.getId());

        return BaseResponse.created(programDTO,uri);
    }
    @GetMapping("/{id}/assets") // for report dropdown
    public BaseResponse<Set<Asset>> getAllAssets(@PathVariable Long id) {
        return BaseResponse.success(bugBountyProgramService.getAllAssets(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteBugBountyProgram(@PathVariable Long id) {
        bugBountyProgramService.deleteBugBountyProgram(id);
        return  BaseResponse.noContent();
    }
}