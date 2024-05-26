package com.turingSecApp.turingSec.dao.repository;

import com.turingSecApp.turingSec.dao.entities.report.CollaboratorEntity;
import com.turingSecApp.turingSec.dao.entities.report.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollaboratorRepository extends JpaRepository<CollaboratorEntity,Long> {
    List<CollaboratorEntity> findByBugBountyReport(Report report);
}
