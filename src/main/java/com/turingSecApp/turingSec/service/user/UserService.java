package com.turingSecApp.turingSec.service.user;


import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.entities.user.HackerEntity;
import com.turingSecApp.turingSec.model.entities.role.Role;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.model.repository.*;
import com.turingSecApp.turingSec.model.repository.program.ProgramRepository;
import com.turingSecApp.turingSec.exception.custom.BadCredentialsException;
import com.turingSecApp.turingSec.exception.custom.EmailAlreadyExistsException;
import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.payload.user.*;
import com.turingSecApp.turingSec.response.program.ProgramDTO;
import com.turingSecApp.turingSec.response.user.AuthResponse;
import com.turingSecApp.turingSec.response.user.UserDTO;
import com.turingSecApp.turingSec.response.user.UserHackerDTO;
import com.turingSecApp.turingSec.service.EmailNotificationService;
import com.turingSecApp.turingSec.service.program.ProgramService;
import com.turingSecApp.turingSec.service.interfaces.IUserService;
import com.turingSecApp.turingSec.util.GlobalConstants;
import com.turingSecApp.turingSec.util.UtilService;
import com.turingSecApp.turingSec.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final EmailNotificationService EmailNotificationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final ProgramService programService;
    private final UserRepository userRepository;
    private final UtilService utilService;
    private final GlobalConstants globalConstants;

    private final HackerRepository hackerRepository;
    private final CompanyRepository companyRepository;
    @Override
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

    // Method to check if user already exists with the provided username or email
    private void checkUserDoesNotExist(RegisterPayload registerPayload) {
        utilService.isUserExistWithUsername(registerPayload.getUsername());
        utilService.isUserExistWithEmail(registerPayload.getEmail());
    }

    // Method to create and save the user entity
    private UserEntity createUserEntity(RegisterPayload registerPayload , boolean activated) {
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
            user.setActivationToken(generateActivationToken());
        }

        // Set user roles
        Set<Role> roles = utilService.getHackerRoles();
        user.setRoles(roles);

        // Save the user
        return userRepository.save(user);
    }


    // Method to create and save the hacker entity
    private HackerEntity createAndSaveHackerEntity(UserEntity user) {
        //Note: To fetch user explicitly to avoid save process instead it updates because there is user entity with actual id not null
        UserEntity fetchedUser = userRepository.findByUsername(user.getUsername()).orElseThrow(()-> new UserNotFoundException("User with username " + user.getUsername() + " not found"));


        HackerEntity hackerEntity = new HackerEntity();
        hackerEntity.setUser(fetchedUser);
        hackerEntity.setFirst_name(fetchedUser.getFirst_name());
        hackerEntity.setLast_name(fetchedUser.getLast_name());
        hackerEntity.setCountry(fetchedUser.getCountry());
        hackerRepository.save(hackerEntity);

        // Accomplish associations between user and hacker
        fetchedUser.setHacker(hackerEntity);
        userRepository.save(fetchedUser);

        return hackerEntity;
    }

    // Method to generate authentication token for the user
    private String generateTokenForUser(UserEntity user) {
        UserDetails userDetails = new CustomUserDetails(user);
        return jwtTokenProvider.generateToken(userDetails);
    }

    // Method to retrieve hacker details by associated user
    private HackerEntity findHackerByUser(UserEntity user) {
        return hackerRepository.findByUser(user);
    }

    ///////////\\\\\\\\\\\

    @Override
    @Transactional // A collection with cascade="all-delete-orphan" was no longer referenced by the owning entity instance: com.turingSecApp.turingSec.dao.entities.user.UserEntity.reports
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

    // Method to accomplish associations between user and hacker
    private void associateUserWithHacker(UserEntity user, HackerEntity hackerEntity) {
        user.setHacker(hackerEntity);
        userRepository.save(user);
    }


    /////////////////////\\\\\\\\\\\\\\\\

    @Override
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
    @Override
    public AuthResponse loginUser(LoginRequest loginRequest) {
        // Find user by email
        UserEntity userEntity = findUserByEmail(loginRequest.getUsernameOrEmail());

        // If user not found by email, try finding by username
        if (userEntity == null) {
            userEntity = findUserByUsername(loginRequest.getUsernameOrEmail());
        }

        // Authenticate user if found
        if (userEntity != null && passwordEncoder.matches(loginRequest.getPassword(), userEntity.getPassword())) {
            // Check if the user is activated
            utilService.checkUserIsActivated(userEntity);

            // Generate token using the user details
            String token = generateTokenForUser(userEntity);

            // Retrieve user and hacker details from the database
            UserEntity userById = findUserById(userEntity.getId());
            HackerEntity hackerFromDB = findHackerByUser(userById);

            // Create and return authentication response
            return utilService.buildAuthResponse(token, userById, hackerFromDB);
        } else {
            // Authentication failed
            throw new BadCredentialsException("Invalid username/email or password.");
        }
    }

    // Method to find user by email
    private UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        // Retrieve authenticated user
        UserEntity user = utilService.getAuthenticatedHacker();

        // Validate current password
        validateCurrentPassword(request, user);

        // Validate and update new password
        updatePassword(request.getNewPassword(), request.getConfirmNewPassword(), user);
    }

    // Method to validate current password
    private void validateCurrentPassword(ChangePasswordRequest request, UserEntity user) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Incorrect current password");
        }
    }

    // Method to validate and update new password
    private void updatePassword(String newPassword,String confirmedPassword, UserEntity user) {
        // Validate new password and confirm new password
        if (!newPassword.equals(confirmedPassword)) {
            throw new BadCredentialsException("New password and confirm new password do not match");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }


    @Override
    public void changeEmail(ChangeEmailRequest request) {
        // Retrieve authenticated user
        UserEntity user = utilService.getAuthenticatedHacker();

        // Validate current password
        validateCurrentPassword(request, user);

        // Check if the new email is already in use
        checkIfEmailExists(request.getNewEmail());

        // Update email
        user.setEmail(request.getNewEmail());
        userRepository.save(user);
    }

   // Method to validate current password
    private void validateCurrentPassword(ChangeEmailRequest request, UserEntity user) {
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Incorrect current password");
        }
    }

    // Method to check if the new email is already in use
    private void checkIfEmailExists(String newEmail) {
        if (userRepository.findByEmail(newEmail) != null) {
            throw new EmailAlreadyExistsException("Email " + newEmail + " is already in use");
        }
    }


    @Override
    public UserHackerDTO updateProfile(UserUpdateRequest userUpdateRequest) {
        UserEntity userEntity = utilService.getAuthenticatedHacker();

        updateProfile(userEntity, userUpdateRequest);
        userRepository.save(userEntity);

        HackerEntity hackerEntity = hackerRepository.findByUser(userEntity);
        if (hackerEntity != null) {
            updateHackerProfile(hackerEntity, userUpdateRequest);
            hackerRepository.save(hackerEntity);
        }

        userRepository.save(userEntity);

        return UserMapper.INSTANCE.toDto(userEntity, hackerEntity);
    }

    private void updateProfile(UserEntity userEntity, UserUpdateRequest userUpdateRequest) {
        userEntity.setUsername(userUpdateRequest.getUsername());
        userEntity.setFirst_name(userUpdateRequest.getFirstName());
        userEntity.setLast_name(userUpdateRequest.getLastName());
        userEntity.setCountry(userUpdateRequest.getCountry());
    }

    private void updateHackerProfile(HackerEntity hackerEntity, UserUpdateRequest userUpdateRequest) {
        hackerEntity.setFirst_name(userUpdateRequest.getFirstName());
        hackerEntity.setLast_name(userUpdateRequest.getLastName());
        hackerEntity.setCountry(userUpdateRequest.getCountry());
        hackerEntity.setCity(userUpdateRequest.getCity());
        hackerEntity.setWebsite(userUpdateRequest.getWebsite());
        hackerEntity.setBio(userUpdateRequest.getBio());
        hackerEntity.setLinkedin(userUpdateRequest.getLinkedin());
        hackerEntity.setTwitter(userUpdateRequest.getTwitter());
        hackerEntity.setGithub(userUpdateRequest.getGithub());
    }

    public String generateNewToken(UserHackerDTO updatedUser) {
        UserDetails userDetailsFromDB = userDetailsService.loadUserByUsername(updatedUser.getUsername());
        // Assuming you have generated a new token here
        return jwtTokenProvider.generateToken(userDetailsFromDB);
    }

    @Override
    public UserDTO getUserById(Long userId) {
        UserEntity user = findUserById(userId);
        return UserMapper.INSTANCE.convert(user);
    }

    @Override
    public UserDTO getCurrentUser() {
        return UserMapper.INSTANCE.convert(utilService.getAuthenticatedHacker());
    }

    @Override
    public List<UserHackerDTO> getAllActiveUsers() {
      return userRepository.findAllByActivated(true)
              .stream()
              .map(userEntity -> UserMapper.INSTANCE.toDto(userEntity, userEntity.getHacker()))
              .collect(Collectors.toList());

    }
    /////////////

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

    @Override
    @Transactional// This annotation ensures that the method is executed within a transactional context, allowing database operations like deletion to be performed reliably.
    public void deleteUser() {
        // Get the authenticated user's username from the security context
        UserEntity authenticatedUser = utilService.getAuthenticatedHacker();

        // Find the user by username
        UserEntity user = findUserByUsername(authenticatedUser.getUsername());

        // Delete the user
        userRepository.delete(user);

        // Clear the authorization header
//        request.removeAttribute("Authorization"); //Not Working , do this (clear auth header) in client side
    }
    /////////////////////////////////Programs\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    @Override
    public List<ProgramDTO> getAllBugBountyPrograms() {
        return programService.getAllBugBountyProgramsAsEntity();
    }

    @Override
    public ProgramDTO getBugBountyProgramById(Long id) {
      return programService.getBugBountyProgramById(id);
    }

    @Override
    public UserEntity findUserByUsername(String username) {
      return   userRepository.findByUsername(username).orElseThrow(()-> new UserNotFoundException("User not found with this username: " + username));
    }


    public CompanyEntity getCompaniesById(Long id) {
        Optional<CompanyEntity> companyEntity = companyRepository.findById(id);
        return companyEntity.orElseThrow(() -> new ResourceNotFoundException("Company not found with id:" + id));
    }

    ///////// Util methods
    private UserEntity findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("User not found with this id: " + userId));
    }

}

