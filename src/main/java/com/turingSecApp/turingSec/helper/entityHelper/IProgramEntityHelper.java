package com.turingSecApp.turingSec.helper.entityHelper;

import com.turingSecApp.turingSec.dao.entities.program.Asset;
import com.turingSecApp.turingSec.dao.entities.program.Program;
import com.turingSecApp.turingSec.dao.entities.program.asset.ProgramAsset;
import com.turingSecApp.turingSec.dao.entities.program.asset.child.*;
import com.turingSecApp.turingSec.dao.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.payload.program.ProgramPayload;

import java.util.Set;

public interface IProgramEntityHelper {

    void removeExistingProgram(CompanyEntity company);
    Program createProgramEntity(ProgramPayload programPayload, CompanyEntity company);
    void deleteBugBountyProgram(Long id, CompanyEntity company);
    <T extends BaseProgramAsset> T saveBaseProgramAsset(T baseProgramAsset);
    <T extends BaseProgramAsset> T setAssetsToBaseProgramAsset(T programAsset, Set<Asset> assets, double price);

    <T extends BaseProgramAsset> void setBaseProgramAssetProperties(T baseProgramAsset, Set<Asset> assetSet, Double assetPrice);
    void setProgramAssetForChildren(ProgramAsset programAsset,
                                    LowProgramAsset lowProgramAsset,
                                    MediumProgramAsset mediumProgramAsset,
                                    HighProgramAsset highProgramAsset,
                                    CriticalProgramAsset criticalProgramAsset);
    ProgramAsset createProgramAsset(
            LowProgramAsset lowProgramAsset,
            MediumProgramAsset mediumProgramAsset,
            HighProgramAsset highProgramAsset,
            CriticalProgramAsset criticalProgramAsset);
}
