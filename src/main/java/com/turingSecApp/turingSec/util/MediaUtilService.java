package com.turingSecApp.turingSec.util;

import com.turingSecApp.turingSec.exception.custom.UnauthorizedException;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.model.entities.user.HackerEntity;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.service.user.factory.UserFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MediaUtilService {
    private final UserFactory userFactory;
    private final UtilService utilService;


    public Long validateHacker(UserDetails userDetails) {
        validateUserDetails(userDetails);
        UserEntity userEntity = (UserEntity) userFactory.getAuthenticatedBaseUser();
        return getHackerId(userEntity);
    }
    private void validateUserDetails(UserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedException();
        }
    }
    private Long getHackerId(UserEntity userEntity) {
        HackerEntity hackerEntity = userEntity.getHacker();
        if (hackerEntity == null) {
            throw new UserNotFoundException("Hacker ID not found for the authenticated user!");
        }
        return hackerEntity.getId();
    }
}
