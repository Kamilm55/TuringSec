package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.response.HackerResponse;
import com.turingSecApp.turingSec.background_file_upload_for_hacker.entity.BackgroundImageForHacker;
import com.turingSecApp.turingSec.background_file_upload_for_hacker.repository.FileRepository;
import com.turingSecApp.turingSec.dao.entities.HackerEntity;
import com.turingSecApp.turingSec.dao.repository.HackerRepository;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.file_upload_for_hacker.entity.ImageForHacker;
import com.turingSecApp.turingSec.file_upload_for_hacker.exception.FileNotFoundException;
import com.turingSecApp.turingSec.file_upload_for_hacker.repository.ImageForHackerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class HackerService {
    private final ModelMapper modelMapper;
    private final HackerRepository hackerRepository;
    private final FileRepository fileRepository;
    private final ImageForHackerRepository imageForHackerRepository;

    public HackerService(ModelMapper modelMapper, HackerRepository hackerRepository, FileRepository fileRepository, ImageForHackerRepository imageForHackerRepository) {
        this.modelMapper = modelMapper;
        this.hackerRepository = hackerRepository;
        this.fileRepository = fileRepository;
        this.imageForHackerRepository = imageForHackerRepository;
    }

    public ResponseEntity<HackerResponse> findById(Long hackerId){
        HackerEntity hackerEntity = hackerRepository.findById(hackerId).orElseThrow(() -> new UserNotFoundException("Hacker is not found with id:" + hackerId));

        System.out.println(hackerEntity);

        HackerResponse hackerResponse = modelMapper.map(hackerEntity, HackerResponse.class);

        BackgroundImageForHacker backgroundImageForHackerByHackerId = fileRepository.findBackgroundImageForHackerByHackerId(hackerId).orElseThrow(() -> new FileNotFoundException("File not found by hacker's id:" + hackerId));
        hackerResponse.setBackgroundImageId(backgroundImageForHackerByHackerId.getId());

        ImageForHacker imageForHackerByHackerId = imageForHackerRepository.findImageForHackerByHackerId(hackerId).orElseThrow( () -> new FileNotFoundException("File not found by hacker's id:" + hackerId));
        hackerResponse.setImageId(imageForHackerByHackerId.getId());

        return ResponseEntity.ok(hackerResponse);


    }
}
