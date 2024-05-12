package com.turingSecApp.turingSec.file_upload.repository;


import com.turingSecApp.turingSec.file_upload.entity.ImageForHacker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageForHackerRepository extends JpaRepository<ImageForHacker, Long> {

    Optional<ImageForHacker> findImageForHackerByHackerId(Long hackerId);

    void delete(ImageForHacker file);
}