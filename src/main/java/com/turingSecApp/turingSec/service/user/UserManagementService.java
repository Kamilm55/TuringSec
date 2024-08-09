package com.turingSecApp.turingSec.service.user;

import com.turingSecApp.turingSec.exception.custom.BadCredentialsException;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.helper.entityHelper.user.IUserEntityHelper;
import com.turingSecApp.turingSec.model.entities.user.HackerEntity;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.model.repository.HackerRepository;
import com.turingSecApp.turingSec.model.repository.UserRepository;
import com.turingSecApp.turingSec.payload.user.*;
import com.turingSecApp.turingSec.response.user.AuthResponse;
import com.turingSecApp.turingSec.response.user.UserHackerDTO;
import com.turingSecApp.turingSec.service.EmailNotificationService;
import com.turingSecApp.turingSec.util.GlobalConstants;
import com.turingSecApp.turingSec.util.UtilService;
import com.turingSecApp.turingSec.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagementService {

    private final UtilService utilService;
    private final UserRepository userRepository;
    private final HackerRepository hackerRepository;
    private final GlobalConstants globalConstants;
    private final EmailNotificationService EmailNotificationService;
    private final JwtUtil jwtTokenProvider;

    private final IUserEntityHelper userEntityHelper;

    private final PasswordEncoder passwordEncoder;

    private final UserDetailsService userDetailsService;

    @Transactional
    public AuthResponse registerHacker(RegisterPayload registerPayload) {
        // Ensure the user doesn't exist
        checkUserDoesNotExist(registerPayload);

        // Populate user entity and save as an inactive user
        UserEntity user = populateUserAndSave(registerPayload,false);

        // Send activation email //todo: change to async event
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

    private UserEntity populateUserAndSave(RegisterPayload registerPayload, boolean activated) {
        // Create the user entity
        UserEntity user = userEntityHelper.createUserEntity(registerPayload,activated);

        // todo: save in one place

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

    public AuthResponse loginUser(LoginRequest loginRequest) {
        // Find user by email
        UserEntity userEntity = userEntityHelper.findByEmail(loginRequest.getUsernameOrEmail());

        // If user not found by email, try finding by username
        if (userEntity == null) {

            System.out.println("works 1:" + userEntity);
            userEntity = userEntityHelper.findUserByUsername(loginRequest.getUsernameOrEmail());
//                    userRepository.findByUsername(loginRequest.getUsernameOrEmail()).orElseThrow(()-> new UserNotFoundException("User not found with this username: " + loginRequest.getUsernameOrEmail()));

        }

         // Authenticate user if found
         checkUserFoundOrNot(userEntity);

         // Ensure password is correct
         checkPassword(loginRequest, userEntity);

        //Check if the user is activated
         utilService.checkUserIsActivated(userEntity);

         // Generate token using the user details
         String token = generateTokenForUser(userEntity);

         // Retrieve user and hacker details from the database
//         UserEntity userById = userEntityHelper.findUserById(userEntity.getId());

         // Create and return authentication response
         return buildAuthResponse(userEntity,token);

    }

    private void checkPassword(LoginRequest loginRequest, UserEntity userEntity) {
        if (!passwordEncoder.matches(loginRequest.getPassword(), userEntity.getPassword())) {
            // Authentication failed
            throw new BadCredentialsException("Invalid username/email or password.");
        }
    }

    private void checkUserFoundOrNot(UserEntity userEntity) {
        if (userEntity == null ) {
            throw new UserNotFoundException("User not found with email/username");
        }
    }

    public void changePassword(ChangePasswordRequest request) {
        // Retrieve authenticated user
        UserEntity user = utilService.getAuthenticatedHackerWithHTTP();

        // Validate current password
        userEntityHelper.validateCurrentPassword(request, user);

        // Validate and update new password
        userEntityHelper.updatePassword(request.getNewPassword(), request.getConfirmNewPassword(), user);
    }


    public void changeEmail(ChangeEmailRequest request) {
        // Retrieve authenticated user
        UserEntity user = utilService.getAuthenticatedHackerWithHTTP();

        // Validate current password
        userEntityHelper.validateCurrentPassword(request, user);

        // Check if the new email is already in use
        userEntityHelper.checkIfEmailExists(request.getNewEmail());

        // Update email
        updateEmail(request, user);
    }

    private void updateEmail(ChangeEmailRequest request, UserEntity user) {
        user.setEmail(request.getNewEmail());
        userRepository.save(user);
    }

    public UserHackerDTO updateProfile(UserUpdateRequest userUpdateRequest) {
        UserEntity userEntity = utilService.getAuthenticatedHackerWithHTTP();
        log.info(String.format("User with id: %s , username: %s updated info. User info before update: %s",userEntity.getId().toString(),userEntity.getUsername(),userEntity));

        userEntityHelper.updateUserProfile(userEntity, userUpdateRequest);
        userRepository.save(userEntity); // todo: save in one place

        HackerEntity hackerEntity = hackerRepository.findByUser(userEntity);

        if (hackerEntity != null) {
            userEntityHelper.updateHackerProfile(hackerEntity, userUpdateRequest);
            hackerRepository.save(hackerEntity);
        }


        UserEntity savedUser = userRepository.save(userEntity);

        log.info(String.format("User with id: %s , username: %s updated info. New user info after update: %s",savedUser.getId().toString(),savedUser.getUsername(),savedUser));

        return UserMapper.INSTANCE.toDto(savedUser, hackerEntity);
    }

    public String generateNewToken(UserHackerDTO updatedUser) {
        UserDetails userDetailsFromDB = userDetailsService.loadUserByUsername(updatedUser.getUsername());

        // Assuming you have generated a new token here
        return jwtTokenProvider.generateToken(userDetailsFromDB);
    }

    public void deleteUser() {
        // Get the authenticated user's username from the security context
        UserEntity authenticatedUser = utilService.getAuthenticatedHackerWithHTTP();

        // Find the user by username
        UserEntity user = userEntityHelper.findUserByUsername(authenticatedUser.getUsername());

        // Delete the user
        userRepository.delete(user);

        // Clear the authorization header
//        request.removeAttribute("Authorization"); // Do this (clear auth header) in client side
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

    // Method to check if user already exists with the provided username or email
    private void checkUserDoesNotExist(RegisterPayload registerPayload) {
        utilService.isUserExistWithUsername(registerPayload.getUsername());
        utilService.isUserExistWithEmail(registerPayload.getEmail());
    }



}
