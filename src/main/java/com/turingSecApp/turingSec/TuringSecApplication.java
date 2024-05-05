package com.turingSecApp.turingSec;

import com.turingSecApp.turingSec.dao.entities.*;
import com.turingSecApp.turingSec.dao.entities.role.Role;
import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
import com.turingSecApp.turingSec.dao.repository.*;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.payload.BugBountyReportPayload;
import com.turingSecApp.turingSec.payload.CollaboratorWithIdPayload;
import com.turingSecApp.turingSec.payload.RegisterPayload;
import com.turingSecApp.turingSec.response.CollaboratorDTO;
import com.turingSecApp.turingSec.service.BugBountyReportService;
import com.turingSecApp.turingSec.service.EmailNotificationService;
import com.turingSecApp.turingSec.service.HackerService;
import com.turingSecApp.turingSec.service.user.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.ws.rs.NotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@SpringBootApplication
@ComponentScan(basePackages = {"com.turingSecApp.turingSec", "com.turingSecApp.turingSec.config"})
@RequiredArgsConstructor
public class TuringSecApplication implements CommandLineRunner {
    private final HackerService hackerService;
    private final UserService userService;
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
    private final BugBountyReportService bugBountyReportService;
    public static void main(String[] args) {
        SpringApplication.run(TuringSecApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Mock Data

        // insert 1 user
        UserEntity user1 = UserEntity.builder()
                .first_name("Kamil")
                .last_name("Memmedov")
                .country("Azerbaijan")
                .username("Username")
                .email("kamilmdov2905@gmail.com")
                .password(passwordEncoder.encode("userPass"))
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

        // insert 2nd user
        RegisterPayload registerPayload = RegisterPayload.builder()
                .firstName("Hackerrrr")
                .lastName("Lastname")
                .country("Azerbaijan")
                .username("Hacker 2")
                .email("kamilmmmdov2905@gmail.com")
                .password(passwordEncoder.encode("userPass"))
                .build();

        userService.insertActiveHacker(registerPayload);

        // insert 2 admins
        AdminEntity admin1 = AdminEntity.builder()
                .first_name("Kamil")
                .last_name("Memmedov")
                .username("admin1_username")
                .password(passwordEncoder.encode("adminPass"))
                .email("kamilmmmdov2905@gmail.com")
                .build();

        AdminEntity admin2 = AdminEntity.builder()
                .first_name("Admin2")
                .last_name("Admin2Last")
                .username("admin2_username")
                .password(passwordEncoder.encode("adminPass "))
                .email("elnarzulfuqarli2001@gmail.com")
                .build();

        //todo: admin must be user create fk and insert
        adminRepository.save(admin1);
        adminRepository.save(admin2);

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
                .fromDate(LocalDate.of(2024, 4, 15))
                .notes("Bug Bounty program for ExampleCompany's web assets.")
                .policy("Responsible Disclosure Policy: Please report any vulnerabilities discovered to security@examplecompany.com.")
                .build();

        bugBountyProgram.setCompany(company1);
        programsRepository.save(bugBountyProgram);

        // insert asset type and strict entity and create relationship
        AssetTypeEntity assetTypeEntity = new AssetTypeEntity();
        assetTypeEntity.setLevel("High");
        assetTypeEntity.setAssetType("Web Application");
        assetTypeEntity.setPrice("$500");
        assetTypeEntity.setBugBountyProgram(programsRepository.findById(bugBountyProgram.getId()).orElse(null));

        assetTypeRepository.save(assetTypeEntity);

        StrictEntity strictEntity = new StrictEntity();
        strictEntity.setProhibitAdded("Prohibits the use of automated scanners without prior permission.");
        strictEntity.setBugBountyProgramForStrict(bugBountyProgram);
        strictRepository.save(strictEntity);

        // update bug bounty program
        bugBountyProgram.setAssetTypes(List.of(Objects.requireNonNull(assetTypeRepository.findById(assetTypeEntity.getId()).orElse(null))));
        bugBountyProgram.setProhibits(List.of(Objects.requireNonNull(strictRepository.findById(strictEntity.getId()).orElse(null))));
        programsRepository.save(bugBountyProgram);




        // insert 2 reports

        BugBountyReportPayload reportPayload = BugBountyReportPayload.builder()
                .asset("ExampleCompany Website")
                .weakness("SQL Injection")
                .severity("High")
                .methodName("POST")
                .proofOfConcept("Injecting SQL code into the login form's username field allows unauthorized access to sensitive data.")
                .discoveryDetails( "Discovered during a routine penetration test of the login functionality.")
                .lastActivity(Date.from(LocalDateTime.parse("2024-05-01T09:46:19.700").toInstant(ZoneOffset.UTC)))
                .reportTitle("Critical SQL Injection Vulnerability in ExampleCompany Website")
                .rewardsStatus("Pending")
                .vulnerabilityUrl("https://example.com/login")
                .userId(1L)
                .collaboratorDTO(
                        List.of(
                                CollaboratorWithIdPayload.builder()
                                        .hackerUsername("Username")
                                        .collaborationPercentage(50.0)
                                        .build(),
                                CollaboratorWithIdPayload.builder()
                                        .hackerUsername("Hacker_2")
                                        .collaborationPercentage(50.0)
                                        .build()
                        )
                )
                .build();

        BugBountyReportPayload reportPayload2 = BugBountyReportPayload.builder()
                .asset("ExampleCompany Mobile App")
                .weakness("Cross-Site Scripting (XSS)")
                .severity("Medium")
                .methodName("GET")
                .proofOfConcept("Injecting JavaScript code through the search input allows execution on other users' sessions.")
                .discoveryDetails("Identified during a code review of the search functionality.")
                .lastActivity(Date.from(LocalDateTime.parse("2024-04-30T15:20:00").toInstant(ZoneOffset.UTC)))
                .reportTitle("XSS Vulnerability in ExampleCompany Mobile App")
                .rewardsStatus("Pending")
                .vulnerabilityUrl("https://example.com/search")
                .userId(2L)
                .collaboratorDTO(
                        List.of(
                                CollaboratorWithIdPayload.builder()
                                        .hackerUsername("securitypro789")
                                        .collaborationPercentage(70.0)
                                        .build(),
                                CollaboratorWithIdPayload.builder()
                                        .hackerUsername("cyberninja007")
                                        .collaborationPercentage(30.0)
                                        .build()
                        )
                )
                .build();


        // insert report
        bugBountyReportService.submitBugBountyReportForTest(reportPayload,1L);
        bugBountyReportService.submitBugBountyReportForTest(reportPayload2, 1L);




        // notify company for approvement
//        emailNotificationService.sendEmail("kamilmdov2905@gmail.com", "subject", "content");
    }
}
