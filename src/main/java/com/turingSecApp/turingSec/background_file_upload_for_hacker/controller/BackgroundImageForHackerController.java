package com.turingSecApp.turingSec.background_file_upload_for_hacker.controller;

import com.turingSecApp.turingSec.background_file_upload_for_hacker.entity.BackgroundImageForHacker;
import com.turingSecApp.turingSec.background_file_upload_for_hacker.response.FileResponse;
import com.turingSecApp.turingSec.background_file_upload_for_hacker.service.IFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/api/background-image-for-hacker")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BackgroundImageForHackerController {
    private final IFileService fileService;

    @PostMapping("/upload")
    public FileResponse uploadVideo(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        Long hackerId = fileService.validateHacker(userDetails);

        // Call the service method to save the video
        return fileService.saveVideoOrImg(file, hackerId);
    }

    @GetMapping("/download/{hackerId}")
    public ResponseEntity<?> downloadVideo(@PathVariable Long hackerId) throws FileNotFoundException {
        BackgroundImageForHacker media = fileService.getVideoById(hackerId);
       return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", media.getContentType())
                .body(
                        media.getFileData()
                );
    }

}