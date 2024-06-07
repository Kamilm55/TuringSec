package com.turingSecApp.turingSec.model.repository;

import com.turingSecApp.turingSec.model.entities.report.CollaboratorEntity;
import com.turingSecApp.turingSec.model.entities.report.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollaboratorRepository extends JpaRepository<CollaboratorEntity,Long> {
    List<CollaboratorEntity> findByBugBountyReport(Report report);
}
