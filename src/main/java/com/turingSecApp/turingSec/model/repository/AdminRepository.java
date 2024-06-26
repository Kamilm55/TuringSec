package com.turingSecApp.turingSec.model.repository;

import com.turingSecApp.turingSec.model.entities.user.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, Long> {

    Optional<AdminEntity> findByUsername(String username);
    AdminEntity findByEmail(String email);

    }
