package com.turingSecApp.turingSec.service.user;


import com.turingSecApp.turingSec.Request.*;
import com.turingSecApp.turingSec.dao.entities.AssetTypeEntity;
import com.turingSecApp.turingSec.dao.entities.BugBountyProgramEntity;
import com.turingSecApp.turingSec.dao.entities.CompanyEntity;
import com.turingSecApp.turingSec.dao.entities.HackerEntity;
import com.turingSecApp.turingSec.dao.entities.role.Role;
import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
import com.turingSecApp.turingSec.dao.repository.*;
import com.turingSecApp.turingSec.exception.custom.*;
import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.payload.RegisterPayload;
import com.turingSecApp.turingSec.response.AuthResponse;
import com.turingSecApp.turingSec.response.BugBountyProgramDTO;
import com.turingSecApp.turingSec.response.UserHackerDTO;
import com.turingSecApp.turingSec.service.EmailNotificationService;
import com.turingSecApp.turingSec.service.ProgramsService;
import com.turingSecApp.turingSec.service.interfaces.IUserService;
import com.turingSecApp.turingSec.util.ProgramMapper;
import com.turingSecApp.turingSec.util.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.turingSecApp.turingSec.util.GlobalConstants.ROOT_LINK;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final HackerRepository hackerRepository;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;
    private final ProgramsRepository programsRepository;

    private final EmailNotificationService emailNotificationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final ProgramsService programsService;

    @Override
    public AuthResponse registerHacker(RegisterPayload registerPayload) {
        // Ensure the user doesn't exist
        isUserExistWithUsername(registerPayload.getUsername());
        isUserExistWithEmail(registerPayload.getEmail());

        UserEntity user = UserEntity.builder()
                .first_name(registerPayload.getFirstName())
                .last_name(registerPayload.getLastName())
                .country(registerPayload.getCountry())
                .username(registerPayload.getUsername())
                .email(registerPayload.getEmail())
                .password(
                        // Encode the password
                        passwordEncoder.encode(registerPayload.getPassword())
                ).activated(false)
                .build();

        // Set user roles
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName("HACKER"));
        user.setRoles(roles);


        // Save the user
        userRepository.save(user);


        //Note: To fetch user explicitly to avoid save process instead it updates because there is user entity with actual id not null
        UserEntity fetchedUser = userRepository.findByUsername(registerPayload.getUsername()).orElseThrow(()-> new UserNotFoundException("User with username " + registerPayload.getUsername() + " not found"));

        // Create and associate , populate hackerEntity entity
        HackerEntity hackerEntity = new HackerEntity();
        hackerEntity.setUser(fetchedUser);
        hackerEntity.setFirst_name(fetchedUser.getFirst_name()); // Set the username in the hackerEntity entity
        hackerEntity.setLast_name(fetchedUser.getLast_name()); // Set the age in the hackerEntity entity
        hackerEntity.setCountry(fetchedUser.getCountry()); // Set the age in the hackerEntity entity


        hackerRepository.save(hackerEntity);

        // Accomplish associations between
        fetchedUser.setHacker(hackerEntity);

        userRepository.save(fetchedUser);

        // Send activation email
        sendActivationEmail(fetchedUser);

        // Generate token for the registered user
        UserDetails userDetails = new CustomUserDetails(fetchedUser);
        String token = jwtTokenProvider.generateToken(userDetails);

        // Retrieve the user ID from CustomUserDetails
        Long userId = ((CustomUserDetails) userDetails).getId();
        UserEntity userById = findUserById(userId);
        HackerEntity hackerFromDB = hackerRepository.findByUser(userById);


        // Create a response map containing the token and user ID
        //refactorThis
        return AuthResponse.builder()
                .accessToken(token)
                .userInfo(
                        UserMapper.INSTANCE.toDto(userById,hackerFromDB)
                )
                .build();
    }

    @Override
    public void insertActiveHacker(RegisterPayload registerPayload) {
        // Ensure the user doesn't exist
        isUserExistWithUsername(registerPayload.getUsername());
        isUserExistWithEmail(registerPayload.getEmail());

        UserEntity user = UserEntity.builder()
                .first_name(registerPayload.getFirstName())
                .last_name(registerPayload.getLastName())
                .country(registerPayload.getCountry())
                .username(registerPayload.getUsername())
                .email(registerPayload.getEmail())
                .password(
                        // Encode the password
                        passwordEncoder.encode(registerPayload.getPassword())
                ).activated(true)
                .build();

        // Set user roles
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName("HACKER"));
        user.setRoles(roles);


        // Save the user
        userRepository.save(user);


        //Note: To fetch user explicitly to avoid save process instead it updates because there is user entity with actual id not null
        UserEntity fetchedUser = userRepository.findByUsername(registerPayload.getUsername()).orElseThrow(()-> new UserNotFoundException("User with username " + registerPayload.getUsername() + " not found"));

        // Create and associate , populate hackerEntity entity
        HackerEntity hackerEntity = new HackerEntity();
        hackerEntity.setUser(fetchedUser);
        hackerEntity.setFirst_name(fetchedUser.getFirst_name()); // Set the username in the hackerEntity entity
        hackerEntity.setLast_name(fetchedUser.getLast_name()); // Set the age in the hackerEntity entity
        hackerEntity.setCountry(fetchedUser.getCountry()); // Set the age in the hackerEntity entity


        hackerRepository.save(hackerEntity);

        // Accomplish associations between
        fetchedUser.setHacker(hackerEntity);

        userRepository.save(fetchedUser);

        // Send activation email
//        sendActivationEmail(fetchedUser);
//
////        // Generate token for the registered user
////        UserDetails userDetails = new CustomUserDetails(fetchedUser);
////        String token = jwtTokenProvider.generateToken(userDetails);
////
//////        // Retrieve the user ID from CustomUserDetails
//////        Long userId = ((CustomUserDetails) userDetails).getId();
//////        UserEntity userById = findUserById(userId);
//////        HackerEntity hackerFromDB = hackerRepository.findByUser(userById);

    }



    private void isUserExistWithEmail(String email) {
        if (userRepository.findByUsername(email).isPresent()) {
            throw new EmailAlreadyExistsException("Email is already taken.");
        }
    }

    private void isUserExistWithUsername(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("Username is already taken.");
        }
    }

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
        // Check if the input is an email
        UserEntity userEntity = userRepository.findByEmail(loginRequest.getUsernameOrEmail());

        // If the input is not an email, check if it's a username
        if(userEntity==null)
            userEntity = userRepository.findByUsername(loginRequest.getUsernameOrEmail()).orElseThrow(()-> new UserNotFoundException("User with username " + loginRequest.getUsernameOrEmail() +" not found"));


        // Authenticate user if found
        if (userEntity != null && passwordEncoder.matches(loginRequest.getPassword(), userEntity.getPassword())) {
            // Check if the user is activated
            if (!userEntity.isActivated()) {
                throw new UserNotActivatedException("User is not activated yet.");
            }

            // Generate token using the user details
            UserDetails userDetails = new CustomUserDetails(userEntity);
            String token = jwtTokenProvider.generateToken(userDetails);

            // Retrieve the user ID from CustomUserDetails
            Long userId = ((CustomUserDetails) userDetails).getId();
            UserEntity userById = findUserById(userId);
            HackerEntity hackerFromDB = hackerRepository.findByUser(userById);


            // Create a response map containing the token and user ID
            //refactorThis
            return AuthResponse.builder()
                    .accessToken(token)
                    .userInfo(
                            UserMapper.INSTANCE.toDto(userById,hackerFromDB)
                    )
                    .build();

        } else {
            // Authentication failed
            throw new BadCredentialsException("Invalid username/email or password.");
        }
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            UserEntity user = userRepository.findByUsername(username).orElseThrow(()-> new UserNotFoundException("User with username " + username + " not found"));

            // Validate current password
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new BadCredentialsException("Incorrect current password");//todo: it must be in security layer
                //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect current password");
            }

            // Validate new password and confirm new password
            if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
                throw new BadCredentialsException("New password and confirm new password do not match");
            }

            // Update password
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

        } else {
            // Handle case where user is not authenticated
            throw new UnauthorizedException();
        }
    }

    @Override
    public void changeEmail(ChangeEmailRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            UserEntity user = userRepository.findByUsername(username).orElseThrow(()-> new UserNotFoundException("User with username " + username + " not found"));

            // Validate password
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("Incorrect current password");//todo: it must be in security layer
            }

           isUserExistWithEmail(request.getNewEmail());

            // Update email
            user.setEmail(request.getNewEmail());
            userRepository.save(user);

        } else {
            // Handle case where user is not authenticated
            throw new UnauthorizedException();
        }
    }

    @Override
    public UserHackerDTO updateProfile(UserUpdateRequest userUpdateRequest) {
        // Get the authenticated user details from the security context
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Extract the username from the authenticated user details
        String username = userDetails.getUsername();

        // Current username can be same
        if(!username.equals(userUpdateRequest.getUsername()))
            isUserExistWithUsername(userUpdateRequest.getUsername());

        // Retrieve the user entity from the repository based on the username
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(()-> new UserNotFoundException("User with username " + username + " not found"));


        // Update the user's first name and last name with the new values

        userEntity.setUsername(userUpdateRequest.getUsername());

        userEntity.setFirst_name(userUpdateRequest.getFirst_name());
        userEntity.setLast_name(userUpdateRequest.getLast_name());
        userEntity.setCountry(userUpdateRequest.getCountry());

        // Save the updated user entity
        userRepository.save(userEntity);

        // Update the corresponding HackerEntity if it exists
        HackerEntity hackerEntity = hackerRepository.findByUser(userEntity);
        if (hackerEntity != null) {
            hackerEntity.setFirst_name(userUpdateRequest.getFirst_name());
            hackerEntity.setLast_name(userUpdateRequest.getLast_name());
            hackerEntity.setCountry(userUpdateRequest.getCountry());
            hackerEntity.setCity(userUpdateRequest.getCity());

            hackerEntity.setWebsite(userUpdateRequest.getWebsite());
//            hackerEntity.setBackground_pic(profileUpdateRequest.getBackground_pic());
//            hackerEntity.setProfile_pic(profileUpdateRequest.getProfile_pic());

            hackerEntity.setBio(userUpdateRequest.getBio());
            hackerEntity.setLinkedin(userUpdateRequest.getLinkedin());
            hackerEntity.setTwitter(userUpdateRequest.getTwitter());
            hackerEntity.setGithub(userUpdateRequest.getGithub());

            hackerEntity.setUser(userEntity);

            hackerRepository.save(hackerEntity);
        }

        userEntity.setHacker(hackerEntity);
        userRepository.save(userEntity);

        return  UserMapper.INSTANCE.toDto(userEntity, hackerEntity);
    }


    public String generateNewToken(UserHackerDTO updatedUser) {
        UserDetails userDetailsFromDB = userDetailsService.loadUserByUsername(updatedUser.getUsername());
        // Assuming you have generated a new token here
        return jwtTokenProvider.generateToken(userDetailsFromDB);
    }

    @Override
    public UserDTO getUserById(Long userId) {
        // Retrieve user information by ID
        Optional<UserEntity> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            return UserMapper.INSTANCE.convert(user);
        } else {
            throw new UserNotFoundException("User is not found with id:" + userId);
        }
    }

    @Override
    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            // Retrieve user details from the database
            return UserMapper.INSTANCE.convert(userRepository.findByUsername(username).orElseThrow(()-> new UserNotFoundException("User with username " + username + " not found")));

        } else {
            // Handle case where user is not authenticated
            // You might return an error response or throw an exception
            throw new UnauthorizedException();
        }
    }

    @Override
    public List<UserDTO> getAllUsers() {
      return userRepository.findAll().stream().map(UserMapper.INSTANCE::convert).collect(Collectors.toList());
    }
    /////////////

    public void sendActivationEmail(UserEntity user) {
        // Generate activation token and save it to the user entity
        String activationToken = generateActivationToken();
        user.setActivationToken(activationToken);
        userRepository.save(user);

        // Send activation email
        String activationLink = ROOT_LINK + "/api/auth/activate?token=" + activationToken;
        String subject = "Activate Your Account";
        String content = "Dear " + user.getFirst_name() + ",\n\n"
                + "Thank you for registering with our application. Please click the link below to activate your account:\n\n"
                + activationLink + "\n\n"
                + "Best regards,\nThe Application Team";

        emailNotificationService.sendEmail(user.getEmail(), subject, content);
    }

    private String generateActivationToken() {
        // You can implement your own token generation logic here
        // This could involve creating a unique token, saving it in the database,
        // and associating it with the user for verification during activation.
        // For simplicity, you can use a library like java.util.UUID.randomUUID().
        return UUID.randomUUID().toString();
    }


    public String findUsernameByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email);
        if (user != null) {
            return user.getUsername();
        } else {
            // Handle the case where the email is not found
            // You may throw an exception or return null based on your application's requirements
            return null;
        }
    }




    public List<CompanyEntity> getAllCompanies() {
        return companyRepository.findAll();
    }



    @Override
    public void deleteUser() {
        // Get the authenticated user's username from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();


        // Find the user by username
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));

        // Delete the user
        userRepository.delete(user);

        // Clear the authorization header
//        request.removeAttribute("Authorization"); //Not Working
    }

    @Override
    public List<BugBountyProgramWithAssetTypeDTO> getAllBugBountyPrograms() {
        List<BugBountyProgramEntity> programs = programsService.getAllBugBountyPrograms();

        // Map BugBountyProgramEntities to BugBountyProgramDTOs
    return programs.stream()
                .map(programEntity -> {
                    BugBountyProgramWithAssetTypeDTO dto = mapToDTO(programEntity);
                    dto.setCompanyId(programEntity.getCompany().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public BugBountyProgramDTO getBugBountyProgramById(Long id) {
      Optional<BugBountyProgramEntity> program = programsRepository.findById(id);

      return ProgramMapper.INSTANCE.toDto(program.orElseThrow(() -> new ResourceNotFoundException("Bug Bounty Program not found")));
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
    private BugBountyProgramWithAssetTypeDTO mapToDTO(BugBountyProgramEntity programEntity) {
        BugBountyProgramWithAssetTypeDTO dto = new BugBountyProgramWithAssetTypeDTO();
//        dto.setId(programEntity.getId());
        dto.setFromDate(programEntity.getFromDate());
        dto.setToDate(programEntity.getToDate());
        dto.setNotes(programEntity.getNotes());
        dto.setPolicy(programEntity.getPolicy());

        // Map associated asset types
        List<AssetTypeDTO> assetTypeDTOs = programEntity.getAssetTypes().stream()
                .map(this::mapAssetTypeToDTO)
                .collect(Collectors.toList());
        dto.setAssetTypes(assetTypeDTOs);

        // You can map other fields as needed

        return dto;
    }
    private AssetTypeDTO mapAssetTypeToDTO(AssetTypeEntity assetTypeEntity) {
        AssetTypeDTO dto = new AssetTypeDTO();
//        dto.setId(assetTypeEntity.getId());
        dto.setLevel(assetTypeEntity.getLevel());
        dto.setAssetType(assetTypeEntity.getAssetType());
        dto.setPrice(assetTypeEntity.getPrice());
        dto.setProgramId(assetTypeEntity.getBugBountyProgram().getId());

        return dto;
    }
}

