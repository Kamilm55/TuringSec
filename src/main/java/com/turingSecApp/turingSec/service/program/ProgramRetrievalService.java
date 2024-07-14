package com.turingSecApp.turingSec.service.program;

import com.turingSecApp.turingSec.helper.entityHelper.program.IProgramEntityHelper;
import com.turingSecApp.turingSec.model.entities.program.Asset;
import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.model.entities.program.asset.ProgramAsset;
import com.turingSecApp.turingSec.model.entities.program.asset.child.BaseProgramAsset;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.repository.program.ProgramRepository;
import com.turingSecApp.turingSec.model.repository.program.asset.ProgramAssetRepository;
import com.turingSecApp.turingSec.response.program.ProgramDTO;
import com.turingSecApp.turingSec.util.UtilService;
import com.turingSecApp.turingSec.util.mapper.ProgramMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProgramRetrievalService {

    private final ProgramRepository programRepository;
    private final UtilService utilService;
    private final ProgramAssetRepository programAssetRepository;
    private final IProgramEntityHelper programEntityHelper;

    public List<ProgramDTO> getCompanyAllBugBountyPrograms() {
        // Retrieve the company associated with the authenticated user
        CompanyEntity company = utilService.getAuthenticatedCompany();

        // Get programs belonging to the company
        List<Program> programList = programRepository.findByCompany(company);

        return ProgramMapper.INSTANCE.toProgramListDTO(programList);
    }

    public Set<Asset> getAllAssets(Long id) {
        Program program = utilService.findProgramById(id);
        Set<ProgramAsset> programAssets = programAssetRepository.findProgramAssetByProgram(program);

        Set<Asset> assets = new HashSet<>();
        for (ProgramAsset programAsset : programAssets) {
            addAssetsFromAllBaseProgramAssets(assets, programAsset);
        }

        return assets;
    }

    private void addAssetsFromAllBaseProgramAssets(Set<Asset> assets, ProgramAsset programAsset) {
        // Iterate all assets
        // Add assets from all base program assets to the assets set
        addAssetsToSet(assets, programAsset.getLowAsset());
        addAssetsToSet(assets, programAsset.getMediumAsset());
        addAssetsToSet(assets, programAsset.getHighAsset());
        addAssetsToSet(assets, programAsset.getCriticalAsset());
    }
    private void addAssetsToSet(Set<Asset> assets, BaseProgramAsset baseProgramAsset) {
        if (baseProgramAsset != null && baseProgramAsset.getAssets() != null) {
            assets.addAll(baseProgramAsset.getAssets());
        }
    }


    // Related to UserService
    public List<ProgramDTO> getAllBugBountyProgramsAsEntity() {
        List<Program> programList = programRepository.findAll();

        return ProgramMapper.INSTANCE.toProgramListDTO(programList);
    }

    public ProgramDTO getBugBountyProgramById(Long id) {
        Program program = utilService.findProgramById(id);

        return ProgramMapper.INSTANCE.toProgramDTO(program);
    }
}
