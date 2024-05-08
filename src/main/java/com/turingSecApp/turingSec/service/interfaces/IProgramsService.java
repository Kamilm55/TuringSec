package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.Request.AssetTypeDTO;
import com.turingSecApp.turingSec.payload.BugBountyProgramWithAssetTypePayload;
import com.turingSecApp.turingSec.response.BugBountyProgramDTO;

import java.util.List;

public interface IProgramsService {
    List<BugBountyProgramDTO> getAllBugBountyPrograms();
    BugBountyProgramDTO createBugBountyProgram(BugBountyProgramWithAssetTypePayload programDTO);
    List<AssetTypeDTO> getCompanyAssetTypes();
    void deleteBugBountyProgram(Long id);
}
