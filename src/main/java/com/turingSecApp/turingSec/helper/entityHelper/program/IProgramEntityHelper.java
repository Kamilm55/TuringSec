package com.turingSecApp.turingSec.helper.entityHelper.program;

import com.turingSecApp.turingSec.model.entities.program.Asset;
import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.model.entities.program.asset.ProgramAsset;
import com.turingSecApp.turingSec.model.entities.program.asset.child.*;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.payload.program.ProgramPayload;
import com.turingSecApp.turingSec.payload.program.asset.AssetPayload;

import java.util.Set;

public interface IProgramEntityHelper {

    void removeExistingProgram(CompanyEntity company);

    Program createProgramEntity(ProgramPayload programPayload, CompanyEntity company);

    <T extends BaseProgramAsset> T saveBaseProgramAsset(T baseProgramAsset);

    <T extends BaseProgramAsset> T setAssetsToBaseProgramAsset(T programAsset, Set<Asset> assets, double price);

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

    void addAssetsToSet(Set<Asset> assets, BaseProgramAsset baseProgramAsset);

    Set<Asset> convertAssetPayloadsToAssets(Set<AssetPayload> assets);
    //
    LowProgramAsset getLowProgramAsset(ProgramPayload programPayload);
    MediumProgramAsset getMediumProgramAsset(ProgramPayload programPayload);
    HighProgramAsset getHighProgramAsset(ProgramPayload programPayload);
    CriticalProgramAsset getCriticalProgramAsset(ProgramPayload programPayload);
}
