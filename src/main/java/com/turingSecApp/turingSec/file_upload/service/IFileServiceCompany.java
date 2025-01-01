package com.turingSecApp.turingSec.file_upload.service;

import com.turingSecApp.turingSec.file_upload.exception.FileNotFoundException;
import com.turingSecApp.turingSec.file_upload.response.FileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFileServiceCompany {
    FileResponse saveVideoOrImg(MultipartFile multipartFile, String companyId) throws IOException;
    <T> T getMediaById(String companyId) throws FileNotFoundException;
}
