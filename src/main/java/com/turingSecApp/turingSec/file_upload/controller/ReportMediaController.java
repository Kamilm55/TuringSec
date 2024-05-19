package com.turingSecApp.turingSec.file_upload.controller;

import com.turingSecApp.turingSec.dao.entities.report.Media;
import com.turingSecApp.turingSec.file_upload.entity.ImageForHacker;
import com.turingSecApp.turingSec.file_upload.response.FileResponse;
import com.turingSecApp.turingSec.file_upload.service.ImageForHackerService;
import com.turingSecApp.turingSec.file_upload.service.ReportMediaService;
import com.turingSecApp.turingSec.util.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/report-media")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ReportMediaController {
    private final ReportMediaService reportMediaService;
    private final UtilService utilService;

    @PostMapping("/{reportId}/upload")
    public List<FileResponse> uploadMedia(@RequestParam("file") List<MultipartFile> files, @AuthenticationPrincipal UserDetails userDetails,@PathVariable Long reportId) throws IOException {
        Long hackerId = utilService.validateHacker(userDetails);

        //todo: check report belongs to this user? do in saveVideoOrImg

        // Call the service method to save the video
        return reportMediaService.saveFiles(files, reportId);
    }

    @GetMapping("/download/{mediaId}")
    public ResponseEntity<?> downloadMedia(@PathVariable Long mediaId) {
        Media media = reportMediaService.getMediaById(mediaId);

        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", media.getContentType())
                .body(
                        media.getFileData()
                );
    }
}
