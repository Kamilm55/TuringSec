package com.turingSecApp.turingSec.dao.repository.program.asset;

import com.turingSecApp.turingSec.dao.entities.program.asset.child.HighProgramAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HPARepository extends JpaRepository<HighProgramAsset,Long> {
}
