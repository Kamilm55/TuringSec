package com.turingSecApp.turingSec.dao.repository.program.asset;

import com.turingSecApp.turingSec.dao.entities.program.asset.child.BaseProgramAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseProgramAssetRepository extends JpaRepository<BaseProgramAsset,Long> {
}
