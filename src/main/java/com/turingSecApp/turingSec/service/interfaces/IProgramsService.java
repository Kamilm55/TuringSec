package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.response.program.AssetTypeDTO;
import com.turingSecApp.turingSec.payload.program.BugBountyProgramWithAssetTypePayload;
import com.turingSecApp.turingSec.response.program.BugBountyProgramDTO;

import java.util.List;

public interface IProgramsService {
    List<BugBountyProgramDTO> getCompanyAllBugBountyPrograms();
    BugBountyProgramDTO createBugBountyProgram(BugBountyProgramWithAssetTypePayload programDTO);
    List<AssetTypeDTO> getCompanyAssetTypes();
    void deleteBugBountyProgram(Long id);
}
