package com.turingSecApp.turingSec.dao.repository.program;

import com.turingSecApp.turingSec.dao.entities.program.Program;
import com.turingSecApp.turingSec.dao.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.dao.entities.report.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramsRepository extends JpaRepository<Program,Long> {
    List<Program> findByCompany(CompanyEntity company);
    Optional<Program> findByReportsContains(Report reports);

    Program findByFromDateAndToDateAndNotesAndPolicyAndCompany(
            LocalDate fromDate, LocalDate toDate, String notes, String policy, CompanyEntity company);
}
