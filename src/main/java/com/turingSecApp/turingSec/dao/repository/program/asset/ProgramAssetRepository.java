package com.turingSecApp.turingSec.dao.repository.program.asset;

import com.turingSecApp.turingSec.dao.entities.program.Program;
import com.turingSecApp.turingSec.dao.entities.program.asset.ProgramAsset;
import com.turingSecApp.turingSec.dao.entities.user.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ProgramAssetRepository extends JpaRepository<ProgramAsset,Long> {
    Set<ProgramAsset> findProgramAssetByProgram(Program program);
}
