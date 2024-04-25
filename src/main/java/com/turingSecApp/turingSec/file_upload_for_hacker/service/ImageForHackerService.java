package com.turingSecApp.turingSec.file_upload_for_hacker.service;

import com.turingSecApp.turingSec.dao.entities.HackerEntity;
import com.turingSecApp.turingSec.dao.repository.HackerRepository;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.file_upload_for_hacker.entity.ImageForHacker;
import com.turingSecApp.turingSec.file_upload_for_hacker.exception.FileNotFoundException;
import com.turingSecApp.turingSec.file_upload_for_hacker.repository.ImageForHackerRepository;
import com.turingSecApp.turingSec.file_upload_for_hacker.response.ImageForHackerResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageForHackerService {
    private final ImageForHackerRepository imageForHackerRepository;
    private final HackerRepository hackerRepository;
    private final ModelMapper modelMapper;

    public ImageForHackerResponse saveVideo(MultipartFile multipartFile, Long hackerId) throws IOException {
        Optional<ImageForHacker> existingFileOptional = imageForHackerRepository.findImageForHackerByHackerId(hackerId);

        HackerEntity hacker = hackerRepository.findById(hackerId).orElseThrow(()-> new UserNotFoundException("Hacker not found with id:"+hackerId));


        if (existingFileOptional.isPresent()) {
            ImageForHacker existingFile = existingFileOptional.get();
            existingFile.setName(multipartFile.getOriginalFilename());
            existingFile.setContentType(multipartFile.getContentType());
            existingFile.setFileData(multipartFile.getBytes());
            ImageForHacker saved = imageForHackerRepository.save(existingFile);

            setHackerPicturesTrueAndSave(hacker);

            ImageForHackerResponse response = modelMapper.map(saved, ImageForHackerResponse.class);
            return response;
        } else {
            ImageForHacker file = new ImageForHacker();
            file.setName(multipartFile.getOriginalFilename());
            file.setContentType(multipartFile.getContentType());
            file.setFileData(multipartFile.getBytes());
            file.setHackerId(hackerId);
            ImageForHacker saved = imageForHackerRepository.save(file);

            setHackerPicturesTrueAndSave(hacker);

            ImageForHackerResponse response = modelMapper.map(saved, ImageForHackerResponse.class);
            return response;
        }
    }


    public ResponseEntity<?> getVideoById(Long id) throws FileNotFoundException {
        ImageForHacker fileOptional = imageForHackerRepository.findById(id).orElseThrow(
                () -> new FileNotFoundException("File not found by id:" + id));
        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", fileOptional.getContentType())
                .body(fileOptional.getFileData());
    }

    // Util methods
    private void setHackerPicturesTrueAndSave(HackerEntity hacker) {
        hacker.setHas_profile_pic(true);
        hacker.setHas_background_pic(true);
        hackerRepository.save(hacker);
    }

}