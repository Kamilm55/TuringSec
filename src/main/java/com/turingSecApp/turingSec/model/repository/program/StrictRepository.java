package com.turingSecApp.turingSec.model.repository.program;

import com.turingSecApp.turingSec.model.entities.program.Prohibit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StrictRepository extends JpaRepository<Prohibit, Long> {

}
