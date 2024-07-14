package com.turingSecApp.turingSec.service.program;

import com.turingSecApp.turingSec.model.entities.program.Asset;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.payload.program.ProgramPayload;
import com.turingSecApp.turingSec.response.program.ProgramDTO;
import com.turingSecApp.turingSec.service.interfaces.IProgramsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProgramService implements IProgramsService {

    private final ProgramRetrievalService programRetrievalService;// For get methods
    private final ProgramManagementService programManagementService;// For other methods

    @Override
    public List<ProgramDTO> getCompanyAllBugBountyPrograms() {
       return programRetrievalService.getCompanyAllBugBountyPrograms();
    }

    @Override
    public ProgramDTO createBugBountyProgram(ProgramPayload programPayload) {
        return programManagementService.createBugBountyProgram(programPayload);
    }

    @Transactional // For commandlineRunner (mock data)
    public void createBugBountyProgramForTest(ProgramPayload programPayload, CompanyEntity company){
        programManagementService.createBugBountyProgramForTest(programPayload,company);
    }

    @Override
    public Set<Asset> getAllAssets(Long id) {
       return programRetrievalService.getAllAssets(id);
    }

    @Override
    public void deleteBugBountyProgram(Long id) {
        programManagementService.deleteBugBountyProgram(id);
    }

    // Related to UserService
    public List<ProgramDTO> getAllBugBountyProgramsAsEntity() {
        return programRetrievalService.getAllBugBountyProgramsAsEntity();
    }

    public ProgramDTO getBugBountyProgramById(Long id) {
        return programRetrievalService.getBugBountyProgramById(id);
    }

}
