package com.turingSecApp.turingSec.model.repository.program.asset;

import com.turingSecApp.turingSec.model.entities.program.asset.child.LowProgramAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LPARepository extends JpaRepository<LowProgramAsset,Long> {
}
