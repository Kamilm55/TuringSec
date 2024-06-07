package com.turingSecApp.turingSec.model.repository.program.asset;

import com.turingSecApp.turingSec.model.entities.program.asset.child.MediumProgramAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MPARepository extends JpaRepository<MediumProgramAsset,Long> {
}
