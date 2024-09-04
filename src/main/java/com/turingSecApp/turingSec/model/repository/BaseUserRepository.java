package com.turingSecApp.turingSec.model.repository;

import com.turingSecApp.turingSec.model.entities.user.BaseUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BaseUserRepository extends JpaRepository<BaseUser, UUID> {
    BaseUser findByEmail(String newEmail);
}