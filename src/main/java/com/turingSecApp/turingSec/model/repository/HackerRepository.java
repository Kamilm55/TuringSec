package com.turingSecApp.turingSec.model.repository;


import com.turingSecApp.turingSec.model.entities.user.HackerEntity;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HackerRepository extends JpaRepository<HackerEntity, Long> {
    HackerEntity findByUser(UserEntity user);

    Optional<HackerEntity> findByUserId(UUID user_id);
}
