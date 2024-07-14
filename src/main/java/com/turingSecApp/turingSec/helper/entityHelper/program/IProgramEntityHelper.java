package com.turingSecApp.turingSec.helper.entityHelper.program;

import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.model.entities.program.asset.ProgramAsset;
import com.turingSecApp.turingSec.model.entities.program.asset.child.CriticalProgramAsset;
import com.turingSecApp.turingSec.model.entities.program.asset.child.HighProgramAsset;
import com.turingSecApp.turingSec.model.entities.program.asset.child.LowProgramAsset;
import com.turingSecApp.turingSec.model.entities.program.asset.child.MediumProgramAsset;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.payload.program.ProgramPayload;

public interface IProgramEntityHelper {

    void removeExistingProgram(CompanyEntity company);
    Program createProgramEntity(ProgramPayload programPayload, CompanyEntity company);
    void setProhibits(ProgramPayload programPayload, Program program);
    void setProgramAsset(ProgramPayload programPayload, Program program);


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
