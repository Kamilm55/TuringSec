package com.turingSecApp.turingSec.dao.repository;

import com.turingSecApp.turingSec.dao.entities.CollaboratorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollaboratorRepository extends JpaRepository<CollaboratorEntity,Long> {
}
