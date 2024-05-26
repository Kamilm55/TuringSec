package com.turingSecApp.turingSec.dao.repository.program.asset;

import com.turingSecApp.turingSec.dao.entities.program.asset.child.CriticalProgramAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CPARepository extends JpaRepository<CriticalProgramAsset,Long> {
}
