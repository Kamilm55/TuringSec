package com.turingSecApp.turingSec.helper.entityHelper.user;


import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.model.entities.role.Role;
import com.turingSecApp.turingSec.model.entities.user.HackerEntity;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.model.repository.HackerRepository;
import com.turingSecApp.turingSec.model.repository.UserRepository;
import com.turingSecApp.turingSec.payload.user.RegisterPayload;
import com.turingSecApp.turingSec.util.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserEntityHelper implements IUserEntityHelper{

    private final UtilService utilService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    @Override
    public UserEntity createUserEntity(RegisterPayload registerPayload, boolean activated) {

        UserEntity user = UserEntity.builder()
                .first_name(registerPayload.getFirstName())
                .last_name(registerPayload.getLastName())
                .country(registerPayload.getCountry())
                .username(registerPayload.getUsername())
                .email(registerPayload.getEmail())
                .password(passwordEncoder.encode(registerPayload.getPassword()))
                .activated(activated)// false for register method
                .build();

        if(activated){ // for inserting active hacker
            user.setActivationToken(utilService.generateActivationToken());
        }

        // Set user roles
        Set<Role> roles = utilService.getHackerRoles();
        user.setRoles(roles);

       return user;
    }

    @Override
    public HackerEntity createHackerEntity(UserEntity user) {
        //Note: To fetch user explicitly to avoid save process instead it updates because there is user entity with actual id not null
        UserEntity fetchedUser = findUserByUsername(user);

        // Create and set basic fields
        HackerEntity hackerEntity = new HackerEntity();
        hackerEntity.setFirst_name(fetchedUser.getFirst_name());
        hackerEntity.setLast_name(fetchedUser.getLast_name());
        hackerEntity.setCountry(fetchedUser.getCountry());

        return hackerEntity;
    }

    @Override
    public void setUserInHackerEntity(UserEntity user, HackerEntity hackerEntity) {
        //Note: To fetch user explicitly to avoid save process instead it updates because there is user entity with actual id not null
        UserEntity fetchedUser = findUserByUsername(user);

        // Accomplish associations between user and hacker
        hackerEntity.setUser(fetchedUser);
    }

    @Override
    public void setHackerInUserEntity(UserEntity user, HackerEntity hackerEntity) {
        // Accomplish associations between user and hacker
        user.setHacker(hackerEntity);
    }
    private UserEntity findUserByUsername(UserEntity user) {
        return userRepository.findByUsername(user.getUsername()).orElseThrow(() -> new UserNotFoundException("User with username " + user.getUsername() + " not found"));
    }
}
