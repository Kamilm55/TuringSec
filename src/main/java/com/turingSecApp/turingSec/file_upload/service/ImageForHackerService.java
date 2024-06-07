package com.turingSecApp.turingSec.file_upload.service;

import com.turingSecApp.turingSec.model.entities.user.HackerEntity;
import com.turingSecApp.turingSec.model.repository.HackerRepository;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.file_upload.entity.ImageForHacker;
import com.turingSecApp.turingSec.file_upload.exception.FileNotFoundException;
import com.turingSecApp.turingSec.file_upload.repository.ImageForHackerRepository;
import com.turingSecApp.turingSec.file_upload.response.FileResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageForHackerService implements IFileService {
    private final ModelMapper modelMapper;
    private final ImageForHackerRepository imageForHackerRepository;
    private final HackerRepository hackerRepository;

    @Override
    public FileResponse saveVideoOrImg(MultipartFile multipartFile, Long hackerId) throws IOException {
        ImageForHacker existingFile = getExistingFileOrEmpty(hackerId);
        HackerEntity hacker = getHackerById(hackerId);

        ImageForHacker updatedFile = updateFileInfo(existingFile, multipartFile, hackerId);

        setHackerPicturesTrueAndSave(hacker);

        return mapToFileResponse(imageForHackerRepository.save(updatedFile));
    }


    private ImageForHacker getExistingFileOrEmpty(Long hackerId) {
        return imageForHackerRepository.findImageForHackerByHackerId(hackerId).orElse(new ImageForHacker());
    }

    private ImageForHacker updateFileInfo(ImageForHacker existingFile, MultipartFile multipartFile, Long hackerId) throws IOException {
        existingFile.setName(multipartFile.getOriginalFilename());
        existingFile.setContentType(multipartFile.getContentType());
        existingFile.setFileData(multipartFile.getBytes());
        existingFile.setHackerId(hackerId);

        return existingFile;
    }

    private HackerEntity getHackerById(Long hackerId) {
        return hackerRepository.findById(hackerId)
                .orElseThrow(() -> new UserNotFoundException("Hacker not found with id:" + hackerId));
    }

    private FileResponse mapToFileResponse(ImageForHacker file) {
        return modelMapper.map(file, FileResponse.class);
    }


    @Override
    public ImageForHacker getMediaById(Long hackerId) throws FileNotFoundException {
        return imageForHackerRepository.findImageForHackerByHackerId(hackerId)
                .orElseThrow(() -> new FileNotFoundException("Video/image file not found for hackerId: " + hackerId));
    }


    // Util
    private void setHackerPicturesTrueAndSave(HackerEntity hacker) {
        hacker.setHas_profile_pic(true);
        // Set other flags if needed
        hackerRepository.save(hacker);
    }
}
