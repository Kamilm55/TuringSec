package com.turingSecApp.turingSec.model.repository.reportMessage;

import com.turingSecApp.turingSec.model.entities.message.FileMessageInReport;
import com.turingSecApp.turingSec.model.entities.message.StringMessageInReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StringMessageInReportRepository extends JpaRepository<StringMessageInReport,Long> {
}
