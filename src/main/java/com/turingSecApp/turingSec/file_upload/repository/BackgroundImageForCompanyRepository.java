package com.turingSecApp.turingSec.file_upload.repository;

import com.turingSecApp.turingSec.file_upload.entity.BackgroundImageForCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BackgroundImageForCompanyRepository extends JpaRepository<BackgroundImageForCompany,Long> {

    Optional<BackgroundImageForCompany> findBackgroundImageForCompanyByCompanyId(String companyId);
}
