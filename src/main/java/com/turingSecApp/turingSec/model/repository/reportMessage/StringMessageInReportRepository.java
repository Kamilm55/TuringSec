package com.turingSecApp.turingSec.model.repository.reportMessage;

import com.turingSecApp.turingSec.model.entities.message.StringMessageInReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StringMessageInReportRepository extends JpaRepository<StringMessageInReport,Long> {
    List<StringMessageInReport> findByReport_Id(Long id);

}
