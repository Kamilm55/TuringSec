package com.turingSecApp.turingSec.model.repository;

import com.turingSecApp.turingSec.model.entities.MockData;
import com.turingSecApp.turingSec.model.entities.user.HackerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MockDataRepository extends JpaRepository<MockData, Long> {
}
