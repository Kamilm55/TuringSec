package com.turingSecApp.turingSec.service.user;

import com.turingSecApp.turingSec.helper.entityHelper.user.IUserEntityHelper;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.model.repository.UserRepository;
import com.turingSecApp.turingSec.response.program.ProgramDTO;
import com.turingSecApp.turingSec.response.user.UserDTO;
import com.turingSecApp.turingSec.response.user.UserHackerDTO;
import com.turingSecApp.turingSec.service.program.ProgramService;
import com.turingSecApp.turingSec.util.UtilService;
import com.turingSecApp.turingSec.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRetrievalService {

    private final UserRepository userRepository;

    private final IUserEntityHelper userEntityHelper;
    private final UtilService utilService;
    private final ProgramService programService;


    public boolean activateAccount(String token) {

        // Retrieve user by activation token
        UserEntity user = userRepository.findByActivationToken(token);

        if (user != null /*&& !user.isActivated()*/) {
            // Activate the user by updating the account status or perform other necessary actions
            user.setActivated(true);
            userRepository.save(user);
            return true;
        }

        return false;
    }

    public UserDTO getUserById(Long userId) {
        UserEntity user = userEntityHelper.findUserById(userId);
        return UserMapper.INSTANCE.convert(user);
    }

    public UserDTO getCurrentUser() {
        return UserMapper.INSTANCE.convert(utilService.getAuthenticatedHackerWithHTTP());
    }

    public List<UserHackerDTO> getAllActiveUsers() {
        return userRepository.findAllByActivated(true)
                .stream()
                .map(userEntity -> UserMapper.INSTANCE.toDto(userEntity, userEntity.getHacker()))
                .collect(Collectors.toList());
    }

    public List<ProgramDTO> getAllBugBountyPrograms() {
        return programService.getAllBugBountyProgramsAsEntity();
    }

    public ProgramDTO getBugBountyProgramById(Long id) {
        return programService.getBugBountyProgramById(id);
    }
}
