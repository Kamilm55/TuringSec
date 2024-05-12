package com.turingSecApp.turingSec.file_upload.repository;

import com.turingSecApp.turingSec.file_upload.entity.BackgroundImageForHacker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BackgroundImageForHackerRepository extends JpaRepository<BackgroundImageForHacker, Long> {
    Optional<BackgroundImageForHacker> findBackgroundImageForHackerByHackerId(Long hackerId);
    BackgroundImageForHacker findById(long id);
}