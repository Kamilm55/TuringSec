package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.dao.entities.program.Asset;
import com.turingSecApp.turingSec.dao.entities.program.Program;
import com.turingSecApp.turingSec.dao.entities.program.asset.ProgramAsset;
import com.turingSecApp.turingSec.payload.program.ProgramPayload;

import java.util.List;
import java.util.Set;

public interface IProgramsService {
    List<Program> getCompanyAllBugBountyPrograms();
    /*BugBountyProgramDTO*/Program createBugBountyProgram(ProgramPayload programDTO);
    Set<Asset> getAllAssets(Long id);
    void deleteBugBountyProgram(Long id);

}
