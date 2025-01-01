package com.turingSecApp.turingSec.file_upload.controller;

import com.turingSecApp.turingSec.file_upload.entity.ImageForHacker;
import com.turingSecApp.turingSec.file_upload.response.ImageForCompanyResponse;
import com.turingSecApp.turingSec.file_upload.service.ImageForCompanyService;
import com.turingSecApp.turingSec.file_upload.service.ImageForHackerService;
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
@RequestMapping("/api/image-for-company")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ImageForCompanyController {
    private final MediaUtilService mediaUtilService;
    private final ImageForHackerService imageForHackerService;
    private final ImageForCompanyService imageForCompanyService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageForCompanyResponse uploadVideo(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        String companyId = mediaUtilService.validateAndGetCompany(userDetails);
        return imageForCompanyService.saveVideoOrImg(file, companyId);
    }

    @GetMapping("/download/{hackerId}")
    public ResponseEntity<?> downloadVideo(@PathVariable Long hackerId) {
        ImageForHacker media = imageForHackerService.getMediaById(hackerId);

        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", media.getContentType())
                .body(
                        media.getFileData()
                );
    }
}
