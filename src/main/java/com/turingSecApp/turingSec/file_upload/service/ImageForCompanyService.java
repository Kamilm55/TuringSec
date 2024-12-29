package com.turingSecApp.turingSec.file_upload.service;

import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.file_upload.entity.ImageForCompany;
import com.turingSecApp.turingSec.file_upload.exception.FileNotFoundException;
import com.turingSecApp.turingSec.file_upload.repository.ImageForCompanyRepository;
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
public class ImageForCompanyService {

    private final ImageForCompanyRepository imageForCompanyRepository;
    private final CompanyRepository companyRepository;
    private final ModelMapper modelMapper;

    public ImageForCompanyResponse saveVideoOrImg(MultipartFile multipartFile, String companyId) throws IOException {
        ImageForCompany existingFile = getExistingFileOrEmpty(companyId);
        CompanyEntity company = getCompanyById(companyId);

        ImageForCompany updatedFile = updateFileInfo(existingFile, multipartFile, companyId);

        setHackerPicturesTrueAndSave(company);

        return mapToFileResponse(imageForCompanyRepository.save(updatedFile));
    }

    private ImageForCompany getExistingFileOrEmpty(String companyId) {
        return imageForCompanyRepository.findImageForCompanyByCompanyId(companyId).orElse(new ImageForCompany());
    }


    private CompanyEntity getCompanyById(String companyId) {
        return companyRepository.findById(UUID.fromString(companyId))
                .orElseThrow(() -> new UserNotFoundException("Hacker not found with id:" + companyId));
    }

    private ImageForCompany updateFileInfo(ImageForCompany existingFile, MultipartFile multipartFile, String companyId) throws IOException {
        existingFile.setName(multipartFile.getOriginalFilename());
        existingFile.setContentType(multipartFile.getContentType());
        existingFile.setFileData(multipartFile.getBytes());
        existingFile.setCompanyId(companyId);

        return existingFile;
    }

    private ImageForCompanyResponse mapToFileResponse(ImageForCompany file) {
        return modelMapper.map(file, ImageForCompanyResponse.class);
    }

    public ImageForCompany getMediaById(String companyId) throws FileNotFoundException {
        return imageForCompanyRepository.findImageForCompanyByCompanyId(companyId)
                .orElseThrow(() -> new FileNotFoundException("Video/image file not found for companyId: " + companyId));
    }


    // Util
    private void setHackerPicturesTrueAndSave(CompanyEntity company) {
        company.setHas_profile_pic(true);
        // Set other flags if needed
        companyRepository.save(company);
    }

}
