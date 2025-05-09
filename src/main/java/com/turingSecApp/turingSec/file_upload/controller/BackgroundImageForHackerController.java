package com.turingSecApp.turingSec.file_upload.controller;

import com.turingSecApp.turingSec.file_upload.entity.BackgroundImageForHacker;
import com.turingSecApp.turingSec.file_upload.response.FileResponse;
import com.turingSecApp.turingSec.file_upload.service.BackgroundImageForHackerService;
import com.turingSecApp.turingSec.util.MediaUtilService;
import com.turingSecApp.turingSec.util.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/background-image-for-hacker")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BackgroundImageForHackerController {
    private final BackgroundImageForHackerService fileService;
    private final MediaUtilService mediaUtilService;

    @PostMapping("/upload")
    public FileResponse uploadVideo(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        Long hackerId = mediaUtilService.validateHacker(userDetails);

        // Call the service method to save the video
        return fileService.saveVideoOrImg(file, hackerId);
    }

    @GetMapping("/download/{hackerId}")
    public ResponseEntity<?> downloadVideo(@PathVariable Long hackerId) {
        BackgroundImageForHacker media = fileService.getMediaById(hackerId);

       return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", media.getContentType())
                .body(
                        media.getFileData()
                );
    }

}