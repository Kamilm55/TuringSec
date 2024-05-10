package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.response.user.HackerDTO;
import com.turingSecApp.turingSec.dao.entities.HackerEntity;
import com.turingSecApp.turingSec.dao.repository.HackerRepository;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.background_file_upload_for_hacker.entity.BackgroundImageForHacker;
import com.turingSecApp.turingSec.background_file_upload_for_hacker.repository.FileRepository;
import com.turingSecApp.turingSec.file_upload_for_hacker.entity.ImageForHacker;
import com.turingSecApp.turingSec.file_upload_for_hacker.repository.ImageForHackerRepository;
import com.turingSecApp.turingSec.service.interfaces.IHackerService;
import com.turingSecApp.turingSec.util.mapper.HackerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HackerService implements IHackerService {
    private final HackerRepository hackerRepository;
    private final FileRepository fileRepository;
    private final ImageForHackerRepository imageForHackerRepository;

    @Override
    public HackerDTO findById(Long hackerId) {
        // Retrieve the hacker entity by ID
        HackerEntity hackerEntity = hackerRepository.findById(hackerId)
                .orElseThrow(() -> new UserNotFoundException("Hacker is not found with id:" + hackerId));

        // Convert the hacker entity to DTO using HackerMapper
        HackerDTO hackerResponse = HackerMapper.INSTANCE.convert(hackerEntity);

        // Set background image ID or null
        BackgroundImageForHacker backgroundImageForHacker = fileRepository.findBackgroundImageForHackerByHackerId(hackerId).orElse(null);
        if (backgroundImageForHacker != null) {
            hackerResponse.setBackgroundImageId(backgroundImageForHacker.getId());
        }

        // Set image ID or null
        ImageForHacker imageForHacker = imageForHackerRepository.findImageForHackerByHackerId(hackerId).orElse(null);
        if (imageForHacker != null) {
            hackerResponse.setImageId(imageForHacker.getId());
        }

        return hackerResponse;
    }
}
