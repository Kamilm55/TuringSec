package com.turingSecApp.turingSec.file_upload.repository;

import com.turingSecApp.turingSec.model.entities.report.Media;
import com.turingSecApp.turingSec.model.entities.report.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<Media,Long> {
    Optional<Media> findMediaByReport(Report report);
    List<Media> findAllByReportId(Long reportId);
}
