package com.turingSecApp.turingSec.controller;


import com.turingSecApp.turingSec.Request.*;
import com.turingSecApp.turingSec.background_file_upload_for_hacker.service.FileService;
import com.turingSecApp.turingSec.dao.entities.*;
import com.turingSecApp.turingSec.dao.entities.role.Role;
import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
import com.turingSecApp.turingSec.dao.repository.HackerRepository;
import com.turingSecApp.turingSec.dao.repository.RoleRepository;
import com.turingSecApp.turingSec.dao.repository.UserRepository;
import com.turingSecApp.turingSec.exception.custom.*;
import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.payload.RegisterPayload;
import com.turingSecApp.turingSec.response.AuthResponse;
import com.turingSecApp.turingSec.response.UserHackerDTO;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.service.ProgramsService;
import com.turingSecApp.turingSec.service.user.CustomUserDetails;
import com.turingSecApp.turingSec.service.user.UserService;
import com.turingSecApp.turingSec.util.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtTokenProvider;
    private final FileService fileService;
    private final ProgramsService programsService;
    private final HackerRepository hackerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @PostMapping("/register/hacker")
    @Transactional
     public BaseResponse<AuthResponse> registerHacker(@RequestBody RegisterPayload payload) {
        // Ensure the user doesn't exist
        if (userRepository.findByUsername(payload.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username is already taken.");
        }

        if (userRepository.findByEmail(payload.getEmail()) != null) {
            throw new EmailAlreadyExistsException("Email is already taken.");
        }


       UserEntity user = UserEntity.builder()
                .first_name(payload.getFirstName())
                .last_name(payload.getLastName())
                .country(payload.getCountry())
                .username(payload.getUsername())
                .email(payload.getEmail())
                .password(
                        // Encode the password
                       passwordEncoder.encode(payload.getPassword())
                ).activated(false)
                .build();

        // Set user roles
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName("HACKER"));
        user.setRoles(roles);


        // Save the user
        userRepository.save(user);


        //Note: To fetch user explicitly to avoid save process instead it updates because there is user entity with actual id not null
        UserEntity fetchedUser = userRepository.findByUsername(payload.getUsername()).orElseThrow(()-> new UserNotFoundException("User with username " + payload.getUsername() + " not found"));

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
        userService.sendActivationEmail(fetchedUser);

        // Generate token for the registered user
        UserDetails userDetails = new CustomUserDetails(fetchedUser);
        String token = jwtTokenProvider.generateToken(userDetails);

        // Retrieve the user ID from CustomUserDetails
        Long userId = ((CustomUserDetails) userDetails).getId();
        UserEntity userById = findUserById(userId);
        HackerEntity hackerFromDB = hackerRepository.findByUser(userById);


        // Create a response map containing the token and user ID
        //refactorThis
       AuthResponse authResponse = AuthResponse.builder()
                .accessToken(token)
                .userInfo(
                        UserMapper.INSTANCE.toDto(userById,hackerFromDB)
                )
                .build();

        return BaseResponse.success(authResponse,"You should receive gmail message for account activation");
    }

    @GetMapping("/activate")
    public ResponseEntity<?> activateAccount(@RequestParam("token") String token) {
        boolean activationResult = userService.activateAccount(token);

        if (!activationResult) {
           throw new  InvalidTokenException();
        }
            return ResponseEntity.ok("Account activated successfully! You can login your account.");
    }

    @PostMapping("/login")
    public BaseResponse<AuthResponse> loginUser(@RequestBody LoginRequest user) {
        // Check if the input is an email
        UserEntity userEntity = userRepository.findByEmail(user.getUsernameOrEmail());

        // If the input is not an email, check if it's a username
        if(userEntity==null)
            userEntity = userRepository.findByUsername(user.getUsernameOrEmail()).orElseThrow(()-> new UserNotFoundException("User with username " + user.getUsernameOrEmail() +" not found"));


        // Authenticate user if found
        if (userEntity != null && passwordEncoder.matches(user.getPassword(), userEntity.getPassword())) {
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
            AuthResponse authResponse = AuthResponse.builder()
                    .accessToken(token)
                    .userInfo(
                            UserMapper.INSTANCE.toDto(userById,hackerFromDB)
                    )
                    .build();

            return BaseResponse.success(authResponse);
        } else {
            // Authentication failed
            throw new BadCredentialsException("Invalid username/email or password.");
        }
    }

    @PostMapping("/change-password")
    public BaseResponse<?> changePassword(@RequestBody ChangePasswordRequest request) {
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

            return BaseResponse.success(null,"Password updated successfully");
        } else {
            // Handle case where user is not authenticated
            throw new UnauthorizedException(); //todo:It must be throw 401 instead of 403
        }
    }

    @PostMapping("/change-email")
    public BaseResponse<?>  changeEmail(@RequestBody ChangeEmailRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            UserEntity user = userRepository.findByUsername(username).orElseThrow(()-> new UserNotFoundException("User with username " + username + " not found"));

            // Validate password
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("Incorrect current password");//todo: it must be in security layer
            }

            if (userRepository.findByEmail(request.getNewEmail()) != null) {
                throw new EmailAlreadyExistsException("Email is already taken.");
            }

            // Update email
            user.setEmail(request.getNewEmail());
            userRepository.save(user);

            return BaseResponse.success(null,"Email updated successfully");
        } else {
            // Handle case where user is not authenticated
            throw new UnauthorizedException(); //todo:It must be throw 401 instead of 403
        }
    }


    @PostMapping("/update-profile")
    public BaseResponse<UserHackerDTO> updateProfile(@RequestBody UserUpdateRequest profileUpdateRequest, HttpServletRequest request) {
        // Get the authenticated user details from the security context
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Extract the username from the authenticated user details
        String username = userDetails.getUsername();

        // Retrieve the user entity from the repository based on the username
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(()-> new UserNotFoundException("User with username " + username + " not found"));

        // Update the user's first name and last name with the new values
        userEntity.setUsername(profileUpdateRequest.getUsername());

        userEntity.setFirst_name(profileUpdateRequest.getFirst_name());
        userEntity.setLast_name(profileUpdateRequest.getLast_name());
        userEntity.setCountry(profileUpdateRequest.getCountry());

        // Save the updated user entity
        userRepository.save(userEntity);

        // Update the corresponding HackerEntity if it exists
        HackerEntity hackerEntity = hackerRepository.findByUser(userEntity);
        if (hackerEntity != null) {
            hackerEntity.setFirst_name(profileUpdateRequest.getFirst_name());
            hackerEntity.setLast_name(profileUpdateRequest.getLast_name());
            hackerEntity.setCountry(profileUpdateRequest.getCountry());
            hackerEntity.setCity(profileUpdateRequest.getCity());

            hackerEntity.setWebsite(profileUpdateRequest.getWebsite());
//            hackerEntity.setBackground_pic(profileUpdateRequest.getBackground_pic());
//            hackerEntity.setProfile_pic(profileUpdateRequest.getProfile_pic());

            hackerEntity.setBio(profileUpdateRequest.getBio());
            hackerEntity.setLinkedin(profileUpdateRequest.getLinkedin());
            hackerEntity.setTwitter(profileUpdateRequest.getTwitter());
            hackerEntity.setGithub(profileUpdateRequest.getGithub());

            hackerEntity.setUser(userEntity);

            hackerRepository.save(hackerEntity);
        }

        userEntity.setHacker(hackerEntity);
        userRepository.save(userEntity);

        UserDetails userDetailsFromDB = userDetailsService.loadUserByUsername(profileUpdateRequest.getUsername());
        // Assuming you have generated a new token here
        String newToken = jwtTokenProvider.generateToken(userDetailsFromDB);

        // Clear the authorization header
//        request.removeAttribute("Authorization");
//        // Set the new token in the Authorization header
//        request.setAttribute("Authorization", "Bearer " + newToken); // Not Working

        UserHackerDTO userHackerDTO = UserMapper.INSTANCE.toDto(userEntity, hackerEntity);

        return BaseResponse.success(
                userHackerDTO,
                "Profile updated successfully. You must update Authorization header (Bearer token) , new token is: " + newToken);
    }


    @GetMapping("/users/{userId}")
    public BaseResponse<UserDTO> getUserById(@PathVariable Long userId) {
        // Retrieve user information by ID
        Optional<UserEntity> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            return BaseResponse.success(UserMapper.INSTANCE.convert(user));
        } else {
            throw new UserNotFoundException("User is not found with id:" + userId);
        }
    }

    @GetMapping("/test")
    public String test() {
        return "test passed";
    }

    @GetMapping("/current-user")
    public BaseResponse<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            // Retrieve user details from the database
            return BaseResponse.success(UserMapper.INSTANCE.convert(userRepository.findByUsername(username).orElseThrow(()-> new UserNotFoundException("User with username " + username + " not found"))));

        } else {
            // Handle case where user is not authenticated
            // You might return an error response or throw an exception
            throw new UnauthorizedException();
        }
    }

    @GetMapping("/allUsers")
    public BaseResponse<List<UserDTO>> getAllUsers() {
        List<UserEntity> userEntities = userService.getAllUsers();

        return BaseResponse.success(userEntities.stream().map(UserMapper.INSTANCE::convert).collect(Collectors.toList()));
    }


    @DeleteMapping("/delete-user")
    public BaseResponse<?> deleteUser(HttpServletRequest request) {
        // Get the authenticated user's username from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();


        // Find the user by username
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));

        // Delete the user
        userRepository.delete(user);

        // Clear the authorization header
//        request.removeAttribute("Authorization"); //Not Working

        return BaseResponse.success(null,"User deleted successfully. You must delete Authorization header (Bearer token)");
    }

    @GetMapping("/programs")
    public BaseResponse<List<BugBountyProgramWithAssetTypeDTO>> getAllBugBountyPrograms() {
        List<BugBountyProgramEntity> programs = programsService.getAllBugBountyPrograms();

        // Map BugBountyProgramEntities to BugBountyProgramDTOs
        List<BugBountyProgramWithAssetTypeDTO> programDTOs = programs.stream()
                .map(programEntity -> {
                    BugBountyProgramWithAssetTypeDTO dto = mapToDTO(programEntity);
                    dto.setCompanyId(programEntity.getCompany().getId());
                    return dto;
                })
                .collect(Collectors.toList());

        return BaseResponse.success(programDTOs);
    }
    @GetMapping("programsById/{id}")
    public BaseResponse<BugBountyProgramEntity> getBugBountyProgramById(@PathVariable Long id) {
        BugBountyProgramEntity program = programsService.getBugBountyProgramById(id);
        return BaseResponse.success(program);
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
