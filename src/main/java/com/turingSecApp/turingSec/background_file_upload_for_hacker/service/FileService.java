package com.turingSecApp.turingSec.background_file_upload_for_hacker.service;

import com.turingSecApp.turingSec.background_file_upload_for_hacker.entity.BackgroundImageForHacker;
import com.turingSecApp.turingSec.background_file_upload_for_hacker.exception.FileNotFoundException;
import com.turingSecApp.turingSec.background_file_upload_for_hacker.repository.FileRepository;
import com.turingSecApp.turingSec.background_file_upload_for_hacker.response.FileResponse;
import com.turingSecApp.turingSec.dao.entities.HackerEntity;
import com.turingSecApp.turingSec.dao.repository.HackerRepository;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
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
public class FileService {
    private final ModelMapper modelMapper;
    private final HackerRepository hackerRepository;
    private final FileRepository fileRepository;


    public FileResponse saveVideoOrImg(MultipartFile multipartFile, Long hackerId) throws IOException {
        Optional<BackgroundImageForHacker> existingFileOptional = fileRepository.findBackgroundImageForHackerByHackerId(hackerId);

        HackerEntity hacker = hackerRepository.findById(hackerId).orElseThrow(()-> new UserNotFoundException("Hacker not found with id:"+hackerId));

        if (existingFileOptional.isPresent()) {
            BackgroundImageForHacker existingFile = existingFileOptional.get();
            existingFile.setName(multipartFile.getOriginalFilename());
            existingFile.setContentType(multipartFile.getContentType());
            existingFile.setFileData(multipartFile.getBytes());

            BackgroundImageForHacker saved = fileRepository.save(existingFile);//save
            setHackerPicturesTrueAndSave(hacker);

            FileResponse response = modelMapper.map(saved, FileResponse.class);


            return response;
        } else {
            BackgroundImageForHacker file = new BackgroundImageForHacker();
            file.setName(multipartFile.getOriginalFilename());
            file.setContentType(multipartFile.getContentType());
            file.setFileData(multipartFile.getBytes());

            file.setHackerId(hackerId); // set hacker id

            BackgroundImageForHacker saved = fileRepository.save(file);// update

            setHackerPicturesTrueAndSave(hacker);
            FileResponse response = modelMapper.map(saved, FileResponse.class);
            return response;
        }

    }

    public ResponseEntity<?> getVideoById(Long id) throws FileNotFoundException {
        BackgroundImageForHacker backgroundImageForHackerOptional = fileRepository.findById(id).orElseThrow(
                () -> new FileNotFoundException("File cannot found by id:" + id));
        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", backgroundImageForHackerOptional.getContentType())
                .body(
                        backgroundImageForHackerOptional.getFileData()
                );
    }
    // Util
    private void setHackerPicturesTrueAndSave(HackerEntity hacker) {
        hacker.setHas_profile_pic(true);
        hacker.setHas_background_pic(true);
        hackerRepository.save(hacker);
    }



}