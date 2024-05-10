package com.turingSecApp.turingSec.dao.repository;

import com.turingSecApp.turingSec.dao.entities.BugBountyProgramEntity;
import com.turingSecApp.turingSec.dao.entities.CompanyEntity;
import com.turingSecApp.turingSec.dao.entities.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramsRepository extends JpaRepository<BugBountyProgramEntity,Long> {
    List<BugBountyProgramEntity> findByCompany(CompanyEntity company);
    Optional<BugBountyProgramEntity> findByReportsContains(ReportEntity reports);

    BugBountyProgramEntity findByFromDateAndToDateAndNotesAndPolicyAndCompany(
            LocalDate fromDate, LocalDate toDate, String notes, String policy, CompanyEntity company);
}
