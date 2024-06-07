package com.turingSecApp.turingSec.helper.entityHelper.program;

import com.turingSecApp.turingSec.model.repository.program.ProgramsRepository;
import com.turingSecApp.turingSec.model.repository.program.asset.AssetRepository;
import com.turingSecApp.turingSec.model.repository.program.asset.ProgramAssetRepository;
import com.turingSecApp.turingSec.util.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProgramTestService {
    private final ProgramsRepository programsRepository;
    private final UtilService utilService;
    private final AssetRepository assetRepository;
    private final ProgramAssetRepository programAssetRepository;
    private final IProgramEntityHelper programEntityHelper;




}
