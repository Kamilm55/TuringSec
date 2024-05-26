package com.turingSecApp.turingSec.dao.repository;

import com.turingSecApp.turingSec.dao.entities.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset,Long> {
}
