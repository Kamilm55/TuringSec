package com.turingSecApp.turingSec;

import com.turingSecApp.turingSec.dao.entities.*;
import com.turingSecApp.turingSec.dao.entities.role.Role;
import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
import com.turingSecApp.turingSec.dao.repository.*;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.service.EmailNotificationService;
import com.turingSecApp.turingSec.service.HackerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.ws.rs.NotFoundException;
import java.time.LocalDate;
import java.util.*;

@SpringBootApplication
@ComponentScan(basePackages = {"com.turingSecApp.turingSec", "com.turingSecApp.turingSec.config"})
@RequiredArgsConstructor
public class TuringSecApplication implements CommandLineRunner {
    private final HackerService hackerService;
    private final HackerRepository hackerRepository;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProgramsRepository programsRepository;
    private final AssetTypeRepository assetTypeRepository;
    private final StrictRepository strictRepository;
    private final EmailNotificationService emailNotificationService;
    public static void main(String[] args) {
        SpringApplication.run(TuringSecApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Mock Data

        // insert 1 company
        Role companyRole = roleRepository.findByName("COMPANY");
        if (companyRole == null) {
            throw new NotFoundException("Company role not found.");
        }

        CompanyEntity company1 = CompanyEntity.builder()
                .first_name("Kenan")
                .last_name("Memmedov")
                .email("string@gmail.com")
                .company_name("Company")
                .job_title("CEO")
                .message("I want to build company")
                .approved(true)
                .password(passwordEncoder.encode("string"))
                .build();

        company1.setRoles(Collections.singleton(companyRole));

        companyRepository.save(company1);



        // insert 1 Bug Bounty program
        BugBountyProgramEntity bugBountyProgram = BugBountyProgramEntity.builder()
                .fromDate(LocalDate.now())
                .notes("Program notes")
                .policy("Policy")
                .build();

        bugBountyProgram.setCompany(company1);
        programsRepository.save(bugBountyProgram);

        // insert asset type and strict entity and create relationship
        AssetTypeEntity assetTypeEntity = new AssetTypeEntity();
        assetTypeEntity.setLevel("low");
        assetTypeEntity.setAssetType("Mobile");
        assetTypeEntity.setPrice("$50");
        assetTypeEntity.setBugBountyProgram(programsRepository.findById(bugBountyProgram.getId()).orElse(null));

        assetTypeRepository.save(assetTypeEntity);

        StrictEntity strictEntity = new StrictEntity();
        strictEntity.setProhibitAdded("prohibits");
        strictEntity.setBugBountyProgramForStrict(bugBountyProgram);
        strictRepository.save(strictEntity);

        // update bug bounty program
        bugBountyProgram.setAssetTypes(List.of(Objects.requireNonNull(assetTypeRepository.findById(assetTypeEntity.getId()).orElse(null))));
        bugBountyProgram.setProhibits(List.of(Objects.requireNonNull(strictRepository.findById(strictEntity.getId()).orElse(null))));
        programsRepository.save(bugBountyProgram);


        // insert 1 user
        UserEntity user1 = UserEntity.builder()
                .first_name("Kamil")
                .last_name("Memmedov")
                .country("Azerbaijan")
                .username("Username")
                .email("string@gmail.com")
                .password(passwordEncoder.encode("string"))
                .activationToken("7203c486-0069-45d4-8857-15a27ad24bee")
                .activated(true)
                .build();
        // Set user roles
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName("HACKER"));
        user1.setRoles(roles);

        userRepository.save(user1);

        //Note: To fetch user explicitly to avoid save process instead it updates because there is user entity with actual id not null
        UserEntity fetchedUser = userRepository.findByUsername(user1.getUsername()).orElseThrow(()-> new UserNotFoundException("User with username " + user1.getUsername() + " not found"));

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

        // insert 2 admins
        AdminEntity admin1 = AdminEntity.builder()
                .first_name("Kamil")
                .last_name("Memmedov")
                .username("admin1_username")
                .password(passwordEncoder.encode("admin"))
                .email("kamilmmmdov2905@gmail.com")
                .build();

        AdminEntity admin2 = AdminEntity.builder()
                .first_name("Admin2")
                .last_name("Admin2Last")
                .username("admin2_username")
                .password(passwordEncoder.encode("admin"))
                .email("elnarzulfuqarli2001@gmail.com")
                .build();

        //todo: admin must be user create fk and insert
        adminRepository.save(admin1);
        adminRepository.save(admin2);

        // notify company for approvement
//        emailNotificationService.sendEmail("kamilmdov2905@gmail.com", "subject", "content");
    }
}
