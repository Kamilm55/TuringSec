package com.turingSecApp.turingSec.file_upload.controller;

import com.turingSecApp.turingSec.dao.entities.HackerEntity;
import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
import com.turingSecApp.turingSec.dao.repository.UserRepository;
import com.turingSecApp.turingSec.exception.custom.UnauthorizedException;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.file_upload.entity.ImageForHacker;
import com.turingSecApp.turingSec.file_upload.response.FileResponse;
import com.turingSecApp.turingSec.file_upload.response.ImageForHackerResponse;
import com.turingSecApp.turingSec.file_upload.service.ImageForHackerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.NotFoundException;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/api/image-for-hacker")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ImageForHackerController {

    private final ImageForHackerService imageForHackerService;
    @PostMapping("/upload")
    public FileResponse uploadVideo(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        Long hackerId = imageForHackerService.validateHacker(userDetails);

        // Call the service method to save the video
        return imageForHackerService.saveVideoOrImg(file, hackerId);
    }

    @GetMapping("/download/{hackerId}")
    public ResponseEntity<?> downloadVideo(@PathVariable Long hackerId) {
        ImageForHacker media = imageForHackerService.getVideoById(hackerId);

        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", media.getContentType())
                .body(
                        media.getFileData()
                );
    }

}