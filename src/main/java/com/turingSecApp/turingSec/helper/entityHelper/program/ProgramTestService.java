package com.turingSecApp.turingSec.helper.entityHelper.program;

import com.turingSecApp.turingSec.dao.entities.program.Asset;
import com.turingSecApp.turingSec.dao.entities.program.asset.ProgramAsset;
import com.turingSecApp.turingSec.dao.entities.program.asset.child.CriticalProgramAsset;
import com.turingSecApp.turingSec.dao.entities.program.asset.child.HighProgramAsset;
import com.turingSecApp.turingSec.dao.entities.program.asset.child.LowProgramAsset;
import com.turingSecApp.turingSec.dao.entities.program.asset.child.MediumProgramAsset;
import com.turingSecApp.turingSec.dao.repository.program.ProgramsRepository;
import com.turingSecApp.turingSec.dao.repository.program.asset.AssetRepository;
import com.turingSecApp.turingSec.dao.repository.program.asset.ProgramAssetRepository;
import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.payload.program.ProgramPayload;
import com.turingSecApp.turingSec.util.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProgramTestService {
    private final ProgramsRepository programsRepository;
    private final UtilService utilService;
    private final AssetRepository assetRepository;
    private final ProgramAssetRepository programAssetRepository;
    private final IProgramEntityHelper programEntityHelper;




}
