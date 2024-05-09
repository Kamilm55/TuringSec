package com.turingSecApp.turingSec;

import com.turingSecApp.turingSec.dao.entities.*;
import com.turingSecApp.turingSec.dao.entities.role.Role;
import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
import com.turingSecApp.turingSec.dao.repository.*;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.payload.*;
import com.turingSecApp.turingSec.service.BugBountyReportService;
import com.turingSecApp.turingSec.service.IEmailNotificationService;
import com.turingSecApp.turingSec.service.ProgramsService;
import com.turingSecApp.turingSec.service.interfaces.IHackerService;
import com.turingSecApp.turingSec.service.interfaces.IUserService;
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
    private final IHackerService hackerService;
    private final IUserService userService;
    private final HackerRepository hackerRepository;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProgramsRepository programsRepository;
    private final ProgramsService programsService;
    private final AssetTypeRepository assetTypeRepository;
    private final StrictRepository strictRepository;
    private final IEmailNotificationService IEmailNotificationService;
    private final BugBountyReportService bugBountyReportService;
    public static void main(String[] args) {
        SpringApplication.run(TuringSecApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        insertMockData();
    }

    private void insertMockData() {
        insertUser();
        insertSecondUser();
        insertAdmins();
        insertCompany();
        insertBugBountyProgram();
        insertReports();

        insertAdditionalData();

//        programsRepository.delete(programsRepository.findById(1L).get());
//
//        programsService.deleteBugBountyProgramForTest(1L);

    }

    private void insertUser() {
        // Insert 1 user
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
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName("HACKER"));
        user1.setRoles(roles);
        userRepository.save(user1);

        UserEntity fetchedUser = userRepository.findByUsername(user1.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User with username " + user1.getUsername() + " not found"));

        HackerEntity hackerEntity = new HackerEntity();
        hackerEntity.setUser(fetchedUser);
        hackerEntity.setFirst_name(fetchedUser.getFirst_name());
        hackerEntity.setLast_name(fetchedUser.getLast_name());
        hackerEntity.setCountry(fetchedUser.getCountry());
        hackerRepository.save(hackerEntity);

        fetchedUser.setHacker(hackerEntity);
        userRepository.save(fetchedUser);
    }

    private void insertSecondUser() {
        // Insert 2nd user
        RegisterPayload registerPayload = RegisterPayload.builder()
                .firstName("Hackerrrr")
                .lastName("Lastname")
                .country("Azerbaijan")
                    .username("Hacker 2")
                .email("kamilmmmdov2905@gmail.com")
                .password("userPass") // encode inside service
                .build();
        userService.insertActiveHacker(registerPayload);
    }

    private void insertAdmins() {
        // Insert 2 admins
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

        adminRepository.save(admin1);
        adminRepository.save(admin2);
    }

    private void insertCompany() {
        // Insert 1 company
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
    }

    private void insertBugBountyProgram() {

        CompanyEntity company = companyRepository.findByEmail("string@gmail.com");
        // Create a new BugBountyProgramWithAssetTypePayload instance
        BugBountyProgramWithAssetTypePayload payload = new BugBountyProgramWithAssetTypePayload();

        // Set data into the payload
        payload.setFromDate(LocalDate.of(2024, 4, 15));
        payload.setToDate(LocalDate.of(2024, 5, 15));
        payload.setPolicy("Responsible Disclosure Policy");
        payload.setNotes("Bug Bounty program for ExampleCompany's web assets.");
//        payload.setCompanyId(1L); // Assuming company ID is 1

        // Create and set asset type payloads
        AssetTypePayload assetTypePayload = new AssetTypePayload();
        assetTypePayload.setLevel("High");
        assetTypePayload.setAssetType("Web Application");
        assetTypePayload.setPrice(500.0); // Assuming price is 500.0
        payload.setAssetTypes(Arrays.asList(assetTypePayload));

        // Create and set prohibits payloads
        StrictPayload strictPayload = new StrictPayload();
        strictPayload.setProhibitAdded("Prohibits the use of automated scanners without prior permission.");
        payload.setProhibits(Arrays.asList(strictPayload));
        programsService.createBugBountyProgramForTest(payload,company);


    }

    private void insertReports() {
        // Insert 2 reports
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

        bugBountyReportService.submitBugBountyReportForTest(reportPayload, 1L,1L);
        bugBountyReportService.submitBugBountyReportForTest(reportPayload2, 1L,2L);
    }

    //
    private void insertAdditionalData() {
        insertAdditionalCompanies();
        insertAdditionalBugBountyPrograms();
        insertAdditionalBugBountyReports();
    }

    private void insertAdditionalCompanies() {
        // Insert 5 additional companies
        Role companyRole = roleRepository.findByName("COMPANY");
        if (companyRole == null) {
            throw new NotFoundException("Company role not found.");
        }

        for (int i = 0; i < 5; i++) {
            CompanyEntity company = CompanyEntity.builder()
                    .first_name("Company " + (i + 1))
                    .last_name("CEO " + (i + 1))
                    .email("company" + (i + 1) + "@example.com")
                    .company_name("Company " + (i + 1))
                    .job_title("CEO")
                    .message("Message " + (i + 1))
                    .approved(true)
                    .password(passwordEncoder.encode("password"))
                    .build();

            company.setRoles(Collections.singleton(companyRole));
            companyRepository.save(company);
        }
    }

    private void insertAdditionalBugBountyPrograms() {
        // Retrieve all companies
        List<CompanyEntity> companies = companyRepository.findAll();

        for (int i = 1; i < 6; i++) {
            // Get the company for this iteration
            CompanyEntity company = companies.get(i);

            // Create a new BugBountyProgramWithAssetTypePayload instance
            BugBountyProgramWithAssetTypePayload payload = new BugBountyProgramWithAssetTypePayload();

            // Set data into the payload
            payload.setFromDate(LocalDate.of(2024, 5, i + 1));
            payload.setPolicy("Policy for Program " + (i + 1));
            payload.setNotes("Notes for Program " + (i + 1));

            // Create and set asset type payloads
            AssetTypePayload assetTypePayload = new AssetTypePayload();
            assetTypePayload.setLevel("Medium");
            assetTypePayload.setAssetType("Asset " + (i + 1));
            assetTypePayload.setPrice((i * 10 + 80.0)); // Set price
            payload.setAssetTypes(Collections.singletonList(assetTypePayload));

            // Create and set prohibits payloads
            StrictPayload strictPayload = new StrictPayload();
            strictPayload.setProhibitAdded("Prohibits for Program " + (i + 1));
            payload.setProhibits(Collections.singletonList(strictPayload));

            // Call the service method to create the bug bounty program
            programsService.createBugBountyProgramForTest(payload, company);
        }
    }


    private void insertAdditionalBugBountyReports() {
        // Insert 5 additional Bug Bounty reports and associate each with a program
        List<BugBountyProgramEntity> programs = programsRepository.findAll();

        for (int i = 0; i < 5; i++) {
            BugBountyProgramEntity program = programs.get(i);

            BugBountyReportPayload reportPayload = BugBountyReportPayload.builder()
                    .asset("Asset " + (i + 1))
                    .weakness("Weakness " + (i + 1))
                    .severity("High")
                    .methodName("POST")
                    .proofOfConcept("Proof of Concept " + (i + 1))
                    .discoveryDetails("Discovery Details " + (i + 1))
                    .lastActivity(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)))
                    .reportTitle("Report " + (i + 1))
                    .rewardsStatus("Pending")
                    .vulnerabilityUrl("https://example.com/report" + (i + 1))
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

            bugBountyReportService.submitBugBountyReportForTest(reportPayload, program.getId(),2L);
        }
    }


}
