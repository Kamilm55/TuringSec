package com.turingSecApp.turingSec.file_upload.service;

import com.turingSecApp.turingSec.file_upload.exception.FileNotFoundException;
import com.turingSecApp.turingSec.file_upload.response.FileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFileService {
    FileResponse saveVideoOrImg(MultipartFile multipartFile, Long hackerId) throws IOException;
    <T> T getMediaById(Long hackerId) throws FileNotFoundException;
}
