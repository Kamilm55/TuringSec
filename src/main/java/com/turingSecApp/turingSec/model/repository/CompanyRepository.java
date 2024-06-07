package com.turingSecApp.turingSec.model.repository;

import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    CompanyEntity findByEmail(String email);


//    Optional<CompanyEntity> findByEmail(String email);

//    CompanyEntity findByBugBountyProgram(Long id);
}
