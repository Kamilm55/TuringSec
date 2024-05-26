package com.turingSecApp.turingSec.dao.repository.report;

import com.turingSecApp.turingSec.dao.entities.report.ReportManual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportManualRepository extends JpaRepository<ReportManual, Long> {
    // Define specific methods for ReportManual entities if needed
}
