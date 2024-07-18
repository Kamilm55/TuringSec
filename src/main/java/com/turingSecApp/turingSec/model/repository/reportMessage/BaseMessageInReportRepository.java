package com.turingSecApp.turingSec.model.repository.reportMessage;

import com.turingSecApp.turingSec.model.entities.message.BaseMessageInReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseMessageInReportRepository extends JpaRepository<BaseMessageInReport,Long> {
}
