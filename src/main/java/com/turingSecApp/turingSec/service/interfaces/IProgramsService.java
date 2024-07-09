package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.model.entities.program.Asset;
import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.payload.program.ProgramPayload;
import com.turingSecApp.turingSec.response.program.ProgramDTO;

import java.util.List;
import java.util.Set;

public interface IProgramsService {
    List<ProgramDTO> getCompanyAllBugBountyPrograms();
    ProgramDTO createBugBountyProgram(ProgramPayload programDTO);
    Set<Asset> getAllAssets(Long id);
    void deleteBugBountyProgram(Long id);

}
