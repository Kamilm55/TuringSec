package com.turingSecApp.turingSec.model.repository;

import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, UUID> {
    CompanyEntity findByEmail(String email);
    Optional<CompanyEntity> findByBugBountyProgramsContains(Program program);

    CompanyEntity findByEmailAndActivated(String email, boolean b);

//    Optional<CompanyEntity> findByEmail(String email);

}
