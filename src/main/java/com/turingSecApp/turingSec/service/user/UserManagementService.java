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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UtilService utilService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final HackerRepository hackerRepository;
    private final GlobalConstants globalConstants;
    private final EmailNotificationService EmailNotificationService;
    private final JwtUtil jwtTokenProvider;

    private final IUserEntityHelper userEntityHelper;

    public AuthResponse registerHacker(RegisterPayload registerPayload) {

        // Ensure the user doesn't exist
        checkUserDoesNotExist(registerPayload);

        // Create and save the user entity
        UserEntity user = createUserEntity(registerPayload , false);

        // Create and save the hacker entity
        HackerEntity hackerEntity = createAndSaveHackerEntity(user);

        // Send activation email //todo: change to async event (kafka)
        sendActivationEmail(user);

        // Generate token for the registered user
        String token = generateTokenForUser(user);

        // Retrieve the user and hacker details from the database
        UserEntity userById = findUserById(user.getId());
        HackerEntity hackerFromDB = findHackerByUser(userById);

        // Build and return the authentication response
        return utilService.buildAuthResponse(token, userById, hackerFromDB);
    }

    public void insertActiveHacker(RegisterPayload registerPayload) {
        // Ensure the user doesn't exist
        checkUserDoesNotExist(registerPayload);

        // Create and Save the user entity
        UserEntity user = createUserEntity(registerPayload, true);

        // Create and save the hacker entity
        HackerEntity hackerEntity = createAndSaveHackerEntity(user);

        // Accomplish associations between user and hacker
        associateUserWithHacker(user, hackerEntity);
    }


//////////////////////////////////////////////////////////////////////
    public void sendActivationEmail(UserEntity user) {
        // Generate activation token and save it to the user entity
        String activationToken = generateActivationToken();
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

    private String generateActivationToken() {
        // You can implement your own token generation logic here
        // This could involve creating a unique token, saving it in the database,
        // and associating it with the user for verification during activation.
        // For simplicity, you can use a library like java.util.UUID.randomUUID().
        return UUID.randomUUID().toString();
    }

    // Method to generate authentication token for the user
    private String generateTokenForUser(UserEntity user) {
        UserDetails userDetails = new CustomUserDetails(user);
        return jwtTokenProvider.generateToken(userDetails);
    }

    ///////// Util methods
    private UserEntity findUserById(Long userId) {
        return utilService.findUserById(userId);
    }

    // Method to check if user already exists with the provided username or email
    private void checkUserDoesNotExist(RegisterPayload registerPayload) {
        utilService.isUserExistWithUsername(registerPayload.getUsername());
        utilService.isUserExistWithEmail(registerPayload.getEmail());
    }

    // Method to retrieve hacker details by associated user
    private HackerEntity findHackerByUser(UserEntity user) {
        return hackerRepository.findByUser(user);
    }

    // Method to create and save the user entity
    private UserEntity createUserEntity(RegisterPayload registerPayload , boolean activated) {
        return userEntityHelper.createUserEntity(registerPayload,activated);
    }

    // Method to create and save the hacker entity
    private HackerEntity createAndSaveHackerEntity(UserEntity user) {
        return userEntityHelper.createAndSaveHackerEntity(user);
    }

    // Method to accomplish associations between user and hacker
    private void associateUserWithHacker(UserEntity user, HackerEntity hackerEntity) {
        user.setHacker(hackerEntity);
        userRepository.save(user);
    }
}
