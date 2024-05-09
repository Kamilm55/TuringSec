package com.turingSecApp.turingSec.background_file_upload_for_hacker.service;

import com.turingSecApp.turingSec.background_file_upload_for_hacker.entity.BackgroundImageForHacker;
import com.turingSecApp.turingSec.background_file_upload_for_hacker.response.FileResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFileService {
    FileResponse saveVideoOrImg(MultipartFile multipartFile, Long hackerId) throws IOException;

    Long validateHacker(UserDetails userDetails);
    BackgroundImageForHacker getVideoById(Long hackerId);
}
