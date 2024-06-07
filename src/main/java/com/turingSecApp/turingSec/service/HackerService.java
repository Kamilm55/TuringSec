package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.response.user.HackerDTO;
import com.turingSecApp.turingSec.model.entities.user.HackerEntity;
import com.turingSecApp.turingSec.model.repository.HackerRepository;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.file_upload.entity.BackgroundImageForHacker;
import com.turingSecApp.turingSec.file_upload.repository.BackgroundImageForHackerRepository;
import com.turingSecApp.turingSec.file_upload.entity.ImageForHacker;
import com.turingSecApp.turingSec.file_upload.repository.ImageForHackerRepository;
import com.turingSecApp.turingSec.service.interfaces.IHackerService;
import com.turingSecApp.turingSec.util.mapper.HackerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HackerService implements IHackerService {
    private final HackerRepository hackerRepository;
    private final BackgroundImageForHackerRepository backgroundImageForHackerRepository;
    private final ImageForHackerRepository imageForHackerRepository;

    @Override
    public HackerDTO findById(Long hackerId) {
        // Retrieve the hacker entity by ID
        HackerEntity hackerEntity = hackerRepository.findById(hackerId)
                .orElseThrow(() -> new UserNotFoundException("Hacker is not found with id:" + hackerId));

        // Convert the hacker entity to DTO using HackerMapper
        HackerDTO hackerResponse = HackerMapper.INSTANCE.convert(hackerEntity);

        // Set background image ID or null
        BackgroundImageForHacker backgroundImageForHacker = backgroundImageForHackerRepository.findBackgroundImageForHackerByHackerId(hackerId).orElse(null);
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
