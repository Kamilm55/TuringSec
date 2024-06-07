package com.turingSecApp.turingSec.model.repository.report;

import com.turingSecApp.turingSec.model.entities.report.ReportCVSS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportCVSSRepository extends JpaRepository<ReportCVSS,Long> {
}
