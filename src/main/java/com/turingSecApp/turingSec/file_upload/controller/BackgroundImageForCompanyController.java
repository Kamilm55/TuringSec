package com.turingSecApp.turingSec.file_upload.controller;

import com.turingSecApp.turingSec.file_upload.entity.BackgroundImageForCompany;
import com.turingSecApp.turingSec.file_upload.response.ImageForCompanyResponse;
import com.turingSecApp.turingSec.file_upload.service.BackgroundImageForCompanyService;
import com.turingSecApp.turingSec.util.MediaUtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/background-image-for-company")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BackgroundImageForCompanyController {
    private final BackgroundImageForCompanyService fileService;
    private final MediaUtilService mediaUtilService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageForCompanyResponse uploadVideo(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        String companyId = mediaUtilService.validateAndGetCompany(userDetails);
        return fileService.saveVideoOrImg(file, companyId);
    }

    @GetMapping("/download/{companyId}")
    public ResponseEntity<?> downloadVideo(@PathVariable String companyId) {
        BackgroundImageForCompany media = fileService.getMediaById(companyId);

        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", media.getContentType())
                .body(
                        media.getFileData()
                );
    }
}
