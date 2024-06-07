package com.turingSecApp.turingSec.model.repository.program;

import com.turingSecApp.turingSec.model.entities.program.StrictEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StrictRepository extends JpaRepository<StrictEntity, Long> {

}
