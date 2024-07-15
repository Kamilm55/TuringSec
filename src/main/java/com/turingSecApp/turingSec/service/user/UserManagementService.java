package com.turingSecApp.turingSec.service.user;

import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.helper.entityHelper.user.IUserEntityHelper;
import com.turingSecApp.turingSec.model.entities.user.HackerEntity;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.model.repository.HackerRepository;
import com.turingSecApp.turingSec.model.repository.UserRepository;
import com.turingSecApp.turingSec.payload.user.RegisterPayload;
import com.turingSecApp.turingSec.response.user.AuthResponse;
import com.turingSecApp.turingSec.service.EmailNotificationService;
import com.turingSecApp.turingSec.util.GlobalConstants;
import com.turingSecApp.turingSec.util.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UtilService utilService;
    private final UserRepository userRepository;
    private final HackerRepository hackerRepository;
    private final GlobalConstants globalConstants;
    private final EmailNotificationService EmailNotificationService;
    private final JwtUtil jwtTokenProvider;

    private final IUserEntityHelper userEntityHelper;

    @Transactional
    public AuthResponse registerHacker(RegisterPayload registerPayload) {
        // Ensure the user doesn't exist
        checkUserDoesNotExist(registerPayload);

        // Populate user entity and save as an inactive user
        UserEntity user = populateUserAndSave(registerPayload,false);

        // Send activation email //todo: change to async event (kafka)
        sendActivationEmail(user);

        // Generate token for the registered user
        String token = generateTokenForUser(user);

        return buildAuthResponse(user, token);
    }

    @Transactional
    public void insertActiveHacker(RegisterPayload registerPayload) {
        // Ensure the user doesn't exist
        checkUserDoesNotExist(registerPayload);

        // Populate user entity and save as an active user
        populateUserAndSave(registerPayload,true);
    }

    private UserEntity populateUserAndSave(RegisterPayload registerPayload,boolean activated) {
        // Create the user entity
        UserEntity user = userEntityHelper.createUserEntity(registerPayload,activated);

        // Save the user
        userRepository.save(user);

        // Create the hacker entity
        HackerEntity hackerEntity = userEntityHelper.createHackerEntity(user);

        // Set user in hacker
        userEntityHelper.setUserInHackerEntity(user,hackerEntity);

        // Save Hacker
        HackerEntity savedHacker = hackerRepository.save(hackerEntity);

        // Set hacker in user
        userEntityHelper.setHackerInUserEntity(user,savedHacker);

        // Update the user
        userRepository.save(user);
        return user;
    }



//////////////////////////////////////////////////////////////////////
    public void sendActivationEmail(UserEntity user) {
        // Generate activation token and save it to the user entity
        String activationToken = utilService.generateActivationToken();
        user.setActivationToken(activationToken);
        userRepository.save(user);

        // Send activation email
        String activationLink = globalConstants.ROOT_LINK + "/api/auth/activate?token=" + activationToken;
        String subject = "Activate Your Account";
        String content = "Dear " + user.getFirst_name() + ",\n\n"
                + "Thank you for registering with our application. Please click the link below to activate your account:\n\n"
                + activationLink + "\n\n"
                + "Best regards,\nThe Application Team";

        EmailNotificationService.sendEmail(user.getEmail(), subject, content);
    }


    private AuthResponse buildAuthResponse(UserEntity user, String token) {
        // Retrieve the user and hacker details from the database
        UserEntity userById = utilService.findUserById(user.getId());
        HackerEntity hackerFromDB = hackerRepository.findByUser(userById);

        // Build and return the authentication response
        return  utilService.buildAuthResponse(token, userById, hackerFromDB);
    }
    // Method to generate authentication token for the user
    private String generateTokenForUser(UserEntity user) {
        UserDetails userDetails = new CustomUserDetails(user);
        return jwtTokenProvider.generateToken(userDetails);
    }

    ///////// Util methods

    // Method to check if user already exists with the provided username or email
    private void checkUserDoesNotExist(RegisterPayload registerPayload) {
        utilService.isUserExistWithUsername(registerPayload.getUsername());
        utilService.isUserExistWithEmail(registerPayload.getEmail());
    }


}
