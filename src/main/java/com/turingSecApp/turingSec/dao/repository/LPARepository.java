package com.turingSecApp.turingSec.dao.repository;

import com.turingSecApp.turingSec.dao.entities.program.asset.child.LowProgramAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LPARepository extends JpaRepository<LowProgramAsset,Long> {
}
