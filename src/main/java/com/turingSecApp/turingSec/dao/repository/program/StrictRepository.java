package com.turingSecApp.turingSec.dao.repository.program;

import com.turingSecApp.turingSec.dao.entities.program.StrictEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StrictRepository extends JpaRepository<StrictEntity, Long> {

}
