package com.turingSecApp.turingSec.file_upload.service;

import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.file_upload.entity.BackgroundImageForCompany;
import com.turingSecApp.turingSec.file_upload.exception.FileNotFoundException;
import com.turingSecApp.turingSec.file_upload.repository.BackgroundImageForCompanyRepository;
import com.turingSecApp.turingSec.file_upload.response.ImageForCompanyResponse;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BackgroundImageForCompanyService {
    private final BackgroundImageForCompanyRepository backgroundImageForCompanyRepository;
    private final CompanyRepository companyRepository;
    private final ModelMapper modelMapper;

    public ImageForCompanyResponse saveVideoOrImg(MultipartFile multipartFile, String companyId) throws IOException {
        BackgroundImageForCompany existingFile = getExistingFileOrEmpty(companyId);
        CompanyEntity company = getCompanyById(companyId);

        BackgroundImageForCompany updatedFile = updateFileInfo(existingFile, multipartFile, companyId);

        setCompanyPicturesTrueAndSave(company);

        return mapToFileResponse(backgroundImageForCompanyRepository.save(updatedFile));
    }

    private BackgroundImageForCompany getExistingFileOrEmpty(String companyId) {
        return backgroundImageForCompanyRepository.findBackgroundImageForCompanyByCompanyId(companyId).orElse(new BackgroundImageForCompany());
    }

    private BackgroundImageForCompany updateFileInfo(BackgroundImageForCompany existingFile, MultipartFile multipartFile, String companyId) throws IOException {
        existingFile.setName(multipartFile.getOriginalFilename());
        existingFile.setContentType(multipartFile.getContentType());
        existingFile.setFileData(multipartFile.getBytes());
        existingFile.setCompanyId(companyId);

        return existingFile;
    }

    private CompanyEntity getCompanyById(String companyId) {
        return companyRepository.findById(UUID.fromString(companyId))
                .orElseThrow(() -> new UserNotFoundException("Company not found with id:" + companyId));
    }

    private ImageForCompanyResponse mapToFileResponse(BackgroundImageForCompany file) {
        return modelMapper.map(file, ImageForCompanyResponse.class);
    }

    public BackgroundImageForCompany getMediaById(String companyId) throws FileNotFoundException {
        return backgroundImageForCompanyRepository.findBackgroundImageForCompanyByCompanyId(companyId)
                .orElseThrow(() -> new FileNotFoundException("Video/image file not found for companyId: " + companyId));
    }

    // Util
    private void setCompanyPicturesTrueAndSave(CompanyEntity company) {
        company.setHas_background_pic(true);
        companyRepository.save(company);
    }
}
