package com.turingSecApp.turingSec.service.user;

import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.model.entities.user.AdminEntity;
import com.turingSecApp.turingSec.model.entities.user.BaseUser;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.model.repository.AdminRepository;
import com.turingSecApp.turingSec.model.repository.BaseUserRepository;
import com.turingSecApp.turingSec.model.repository.CompanyRepository;
import com.turingSecApp.turingSec.model.repository.UserRepository;
import com.turingSecApp.turingSec.exception.custom.CompanyNotFoundException;
import com.turingSecApp.turingSec.util.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    private final BaseUserRepository baseUserRepository;
    private final UtilService utilService;

    private UUID id;

    public UUID getId() {
        return id;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String uuidOfBaseUserAsUsername = username;
        log.info("Username/uuid of baseUser from jwt token: " + uuidOfBaseUserAsUsername);

        // todo: check error if parsing throw error  -> InvalidUUIDFormatException
        // Use UUID(string format) as username for all entities , in jwt contains user id instead of username
        BaseUser baseUser = baseUserRepository.findById(utilService.convertToUUID(uuidOfBaseUserAsUsername)).orElseThrow(()-> new UserNotFoundException("Base User not found with id: " + username + " ,from jwt token"));

        log.info("(loadUserByUsername) -> user: " + baseUser);

        return new CustomUserDetails(baseUser);
    }
}