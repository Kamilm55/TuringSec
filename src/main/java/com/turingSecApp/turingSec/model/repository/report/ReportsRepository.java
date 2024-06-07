package com.turingSecApp.turingSec.model.repository.report;

import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
//This repository can handle common operations related to ReportEntity, such as CRUD operations or any other queries that apply to all types of reports.
public interface ReportsRepository extends JpaRepository<Report, Long> {
    List<Report> findByUser(UserEntity user);
    List<Report> findByBugBountyProgram(Program program);
    @Query("SELECT r FROM Report r WHERE r.bugBountyProgram IN :programs")
    List<Report> findByBugBountyProgramIn(Collection<Program> programs);
    void deleteAllByUser(UserEntity user);
}
