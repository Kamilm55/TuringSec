package com.turingSecApp.turingSec.model.repository.reportMessage;

import com.turingSecApp.turingSec.model.entities.message.FileMessageInReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMessageInReportRepository extends JpaRepository<FileMessageInReport,Long> {
}
