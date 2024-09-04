package com.turingSecApp.turingSec.model.repository;

import com.turingSecApp.turingSec.model.entities.user.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, UUID> {

    Optional<AdminEntity> findByUsername(String username);
    AdminEntity findByEmail(String email);

    }
