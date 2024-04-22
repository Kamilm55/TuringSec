package com.turingSecApp.turingSec.dao.repository;

import com.turingSecApp.turingSec.dao.entities.StrictEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StrictRepository extends JpaRepository<StrictEntity, Long> {

}
