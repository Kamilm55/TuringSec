package com.turingSecApp.turingSec.file_upload.service;

import com.turingSecApp.turingSec.file_upload.response.FileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IMultiFileService extends IFileService{
    List<FileResponse> saveFiles(List<MultipartFile> fileList, Long reportId) throws IOException;
}
