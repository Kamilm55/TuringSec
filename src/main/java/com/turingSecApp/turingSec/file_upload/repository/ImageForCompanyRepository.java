package com.turingSecApp.turingSec.file_upload.repository;

import com.turingSecApp.turingSec.file_upload.entity.ImageForCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageForCompanyRepository extends JpaRepository<ImageForCompany, String> {
    Optional<ImageForCompany> findImageForCompanyByCompanyId(String companyId);
}
