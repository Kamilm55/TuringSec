package com.turingSecApp.turingSec.dao.repository;

import com.turingSecApp.turingSec.dao.entities.report.ReportCVSS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportCVSSRepository extends JpaRepository<ReportCVSS,Long> {
}
