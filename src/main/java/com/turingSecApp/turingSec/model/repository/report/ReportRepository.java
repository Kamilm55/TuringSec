package com.turingSecApp.turingSec.model.repository.report;

import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.model.entities.report.enums.REPORTSTATUSFORCOMPANY;
import com.turingSecApp.turingSec.model.entities.report.enums.REPORTSTATUSFORUSER;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
//This repository can handle common operations related to ReportEntity, such as CRUD operations or any other queries that apply to all types of reports.
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByUser(UserEntity user);
    List<Report> findByUserAndStatusForUser(UserEntity user, REPORTSTATUSFORUSER reportstatusforuser);

    List<Report> findByBugBountyProgram(Program program);
    @Query("SELECT r FROM Report r WHERE r.bugBountyProgram IN :programs")
    List<Report> findByBugBountyProgramIn(Collection<Program> programs);
    void deleteAllByUser(UserEntity user);

//    List<StringMessageInReport> findByContent(Report report);

    Optional<Report> findByRoom(String room);


    List<Report> findByBugBountyProgramCompany(CompanyEntity company);
    List<Report> findByBugBountyProgramCompanyAndStatusForCompany(CompanyEntity company, REPORTSTATUSFORCOMPANY reportstatusforcompany);

    Optional<Report> findByCreatedAt(LocalDate startDate);
}
