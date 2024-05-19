package com.turingSecApp.turingSec.file_upload.repository;

import com.turingSecApp.turingSec.dao.entities.report.Media;
import com.turingSecApp.turingSec.dao.entities.report.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<Media,Long> {
    Optional<Media> findMediaByReport(ReportEntity report);
    List<Media> findAllByReportId(Long reportId);
}
