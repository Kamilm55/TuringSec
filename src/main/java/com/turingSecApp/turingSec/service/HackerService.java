package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.response.HackerDTO;
import com.turingSecApp.turingSec.response.HackerResponse;
import com.turingSecApp.turingSec.background_file_upload_for_hacker.entity.BackgroundImageForHacker;
import com.turingSecApp.turingSec.background_file_upload_for_hacker.repository.FileRepository;
import com.turingSecApp.turingSec.dao.entities.HackerEntity;
import com.turingSecApp.turingSec.dao.repository.HackerRepository;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.file_upload_for_hacker.entity.ImageForHacker;
import com.turingSecApp.turingSec.file_upload_for_hacker.exception.FileNotFoundException;
import com.turingSecApp.turingSec.file_upload_for_hacker.repository.ImageForHackerRepository;
import com.turingSecApp.turingSec.service.interfaces.IHackerService;
import com.turingSecApp.turingSec.util.HackerMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HackerService implements IHackerService {
    private final HackerRepository hackerRepository;
    private final FileRepository fileRepository;
    private final ImageForHackerRepository imageForHackerRepository;

    @Override
    public HackerDTO findById(Long hackerId){
        HackerEntity hackerEntity = hackerRepository.findById(hackerId).orElseThrow(() -> new UserNotFoundException("Hacker is not found with id:" + hackerId));

        HackerDTO hackerResponse = HackerMapper.INSTANCE.convert(hackerEntity);

        //set bg img id or null
        BackgroundImageForHacker backgroundImageForHackerByHackerId = fileRepository.findBackgroundImageForHackerByHackerId(hackerId).orElse(null);
        /*.orElseThrow(() -> new FileNotFoundException("File not found by hacker's id:" + hackerId));*/
        if(backgroundImageForHackerByHackerId!=null)
            hackerResponse.setBackgroundImageId(backgroundImageForHackerByHackerId.getId());
//        else
//            hackerResponse.setBackgroundImageId(null);

        //set img id or null
        ImageForHacker imageForHackerByHackerId = imageForHackerRepository.findImageForHackerByHackerId(hackerId).orElse(null);
        /*.orElseThrow( () -> new FileNotFoundException("File not found by hacker's id:" + hackerId));*/
       if(imageForHackerByHackerId!=null)
           hackerResponse.setImageId(imageForHackerByHackerId.getId());
       else
           hackerResponse.setImageId(null);

        return hackerResponse;
    }
}
