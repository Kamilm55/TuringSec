package com.turingSecApp.turingSec.model.repository.program.asset;

import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.model.entities.program.asset.ProgramAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ProgramAssetRepository extends JpaRepository<ProgramAsset,Long> {
    Set<ProgramAsset> findProgramAssetByProgram(Program program);
}
