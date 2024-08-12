package com.turingSecApp.turingSec;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.NameBasedGenerator;
import com.turingSecApp.turingSec.exception.custom.BadCredentialsException;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.helper.entityHelper.user.IUserEntityHelper;
import com.turingSecApp.turingSec.model.entities.user.*;
import com.turingSecApp.turingSec.model.repository.*;
import com.turingSecApp.turingSec.model.repository.report.ReportRepository;
import com.turingSecApp.turingSec.payload.user.ChangePasswordRequest;
import com.turingSecApp.turingSec.payload.user.LoginRequest;
import com.turingSecApp.turingSec.response.user.AuthResponse;
import com.turingSecApp.turingSec.service.program.ProgramService;
import com.turingSecApp.turingSec.service.interfaces.IHackerService;
import com.turingSecApp.turingSec.service.interfaces.IMockDataService;
import com.turingSecApp.turingSec.service.interfaces.IUserService;
import com.turingSecApp.turingSec.service.user.CustomUserDetails;
import com.turingSecApp.turingSec.util.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@SpringBootApplication
@ComponentScan(basePackages = {"com.turingSecApp.turingSec", "com.turingSecApp.turingSec.config"})
@RequiredArgsConstructor
@Slf4j
public class TuringSecApplication implements CommandLineRunner {
    private final IHackerService hackerService;
    private final IUserService userService;
    private final HackerRepository hackerRepository;
    private final RoleRepository roleRepository;
    private final IUserEntityHelper userEntityHelper;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReportRepository reportRepository;
    private final JwtUtil jwtTokenProvider;
    private final ProgramService programService;
    private final UtilService utilService;
    private  final IMockDataService mockDataService;
    public static void main(String[] args) {
        SpringApplication.run(TuringSecApplication.class, args);
    }

    @Override
    @Transactional
    public void run(String... args){
        mockDataService.insertMockData();

        // Print Roles
        UserEntity hacker1 = userRepository.findByEmail("kamilmmmdov2905@gmail.com");
        if(hacker1!=null) {
            System.out.println("hacker roles: " + hacker1.getRoles().toString());
             System.out.println("hacker entity for user" + hacker1.getHacker());
        }

        CompanyEntity company = companyRepository.findByEmail("string@gmail.com");
        if(company!=null){
            System.out.println("company roles: "+company.getRoles().toString());
        }

        Optional<AdminEntity> admin1 = adminRepository.findByUsername("admin1_username");
        admin1.ifPresent(adminEntity -> System.out.println("admin roles: " + adminEntity.getRoles().toString()));

        //
        // todo: Implement Factory method pattern for IBaseUser
        //  inherit from base user class as different tables
        //  change jwt identifier to globalUserID (UUIDv5)


        System.out.println("todo: Implement Factory method pattern for baseUser");

        IBaseUser IBaseUser1 = hacker1;
        IBaseUser IBaseUser2 = company;
        IBaseUser IBaseUser3 = admin1.get();

        System.out.println(IBaseUser1);
        System.out.println(IBaseUser2);
        System.out.println(IBaseUser3);

        System.out.println(IBaseUser1.equals(hacker1) + " | " + IBaseUser2.equals(company) + " | " + IBaseUser3.equals(admin1.get()));

        NameBasedGenerator generator = Generators.nameBasedGenerator(NameBasedGenerator.NAMESPACE_DNS);
        String name = "baseUserID";
        UUID uuid = generator.generate(name);
        System.out.println("Generated UUIDv5 for base user id: " + uuid.toString());


        //todo: https://stackoverflow.com/questions/45402742/git-merging-when-to-use-ours-strategy-ours-option-and-theirs-option
        // change app to version abccfb2

        // TEST
        System.out.println("TEST");
        String username = "Username";
//        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found with this username: " + username));

//        System.out.println(userEntity);

        UserEntity userEntity1 = userEntityHelper.findUserByUsername(username);

        System.out.println(userEntity1);

        //
        // Find user by email
        UserEntity userEntity = userEntityHelper.findByEmail(username);

        // If user not found by email, try finding by username
        if (userEntity == null) {
            userEntity = userEntityHelper.findUserByUsername(username);
        }

        // Authenticate user if found
        checkUserFoundOrNot(userEntity);

        // Ensure password is correct
//        checkPassword(loginRequest, userEntity);

        //Check if the user is activated
        utilService.checkUserIsActivated(userEntity);

        // Generate token using the user details
        String token = generateTokenForUser(userEntity);

        // Retrieve user and hacker details from the database
//         UserEntity userById = userEntityHelper.findUserById(userEntity.getId());

        // Create and return authentication response
        System.out.println(buildAuthResponse(userEntity, token));


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


}
