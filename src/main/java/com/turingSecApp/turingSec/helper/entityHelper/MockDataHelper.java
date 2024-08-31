package com.turingSecApp.turingSec.helper.entityHelper;

import com.turingSecApp.turingSec.exception.custom.CompanyNotFoundException;
import com.turingSecApp.turingSec.model.entities.MockData;
import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.model.entities.report.embedded.ProofOfConcept;
import com.turingSecApp.turingSec.model.entities.report.embedded.ReportWeakness;
import com.turingSecApp.turingSec.model.entities.role.Role;
import com.turingSecApp.turingSec.model.entities.user.AdminEntity;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.repository.*;
import com.turingSecApp.turingSec.model.repository.report.ReportRepository;
import com.turingSecApp.turingSec.payload.program.ProgramPayload;
import com.turingSecApp.turingSec.payload.program.ProhibitPayload;
import com.turingSecApp.turingSec.payload.program.asset.AssetPayload;
import com.turingSecApp.turingSec.payload.program.asset.BaseProgramAssetPayload;
import com.turingSecApp.turingSec.payload.program.asset.ProgramAssetPayload;
import com.turingSecApp.turingSec.payload.report.ReportManualPayload;
import com.turingSecApp.turingSec.payload.report.child.ReportAssetPayload;
import com.turingSecApp.turingSec.payload.user.RegisterPayload;
import com.turingSecApp.turingSec.service.interfaces.IReportService;
import com.turingSecApp.turingSec.service.interfaces.IUserService;
import com.turingSecApp.turingSec.service.program.ProgramService;
import com.turingSecApp.turingSec.util.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MockDataHelper {
    private final MockDataRepository mockDataRepository;
    private final IUserService userService;
    private final ProgramService programService;
    private final UtilService utilService;
    private final PasswordEncoder passwordEncoder;
    private final IReportService reportService;

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;
    private final ReportRepository reportRepository;

    @Transactional // to achieve transaction we call this method from outside
    public void insertAllTransactionally(MockData mockData) {
        insertRoles(); // todo:change role structure
        insertUsersAndHackers();
        insertCompany();
        insertAdmins();
        insertProgram();

        insertReports();

        mockData.setInsertedMockNumber(1);
        mockDataRepository.save(mockData); // save mock data in table with value 1
    }
    public void insertReports() {
        // Insert 1 mock manual report

        // Populate payload
        ReportManualPayload reportManualPayload = new ReportManualPayload();
        reportManualPayload.setLastActivity(Date.from(Instant.now()));
        reportManualPayload.setRewardsStatus("200$");
        reportManualPayload.setReportTemplate("report template");
        reportManualPayload.setCollaboratorPayload(new ArrayList<>());

        ReportAssetPayload reportAssetPayload = new ReportAssetPayload("domain","y.com");
        reportManualPayload.setReportAssetPayload(reportAssetPayload);

        ReportWeakness reportWeakness = new ReportWeakness("Weakness type","Weaknes name");
        reportManualPayload.setWeakness(reportWeakness);

        ProofOfConcept proofOfConcept = new ProofOfConcept("Title","Example.com","DESCRIPTION");
        reportManualPayload.setProofOfConcept(proofOfConcept);
        reportManualPayload.setSeverity("SEVERITY");

        // Submit
        try {
            reportService.submitManualReportForTest(new ArrayList<>(),reportManualPayload,1L);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void insertRoles() {
        // Check if roles already exist to avoid duplication
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role("HACKER"));
            roleRepository.save(new Role("COMPANY"));
            roleRepository.save(new Role("ADMIN"));
        }
    }


    public void insertUsersAndHackers() {
        // Insert 1st user
        RegisterPayload registerPayload1 = RegisterPayload.builder()
                .firstName("Kamil")
                .lastName("Memmedov")
                .country("Azerbaijan")
                .username("Username")
                .email("kamilmdov2905@gmail.com")
                .password("userPass") // encode inside service
                .build();
        userService.insertActiveHacker(registerPayload1); // activated true

        // Insert 2nd user
        RegisterPayload registerPayload2 = RegisterPayload.builder()
                .firstName("Hackerrrr")
                .lastName("Lastname")
                .country("Azerbaijan")
                .username("Hacker_2")
                .email("kamilmmmdov2905@gmail.com")
                .password("userPass") // encode inside service
                .build();
        userService.insertActiveHacker(registerPayload2);
    }


    public void insertAdmins() {
        // Insert 2 admins
        AdminEntity admin1 = AdminEntity.builder()
                .first_name("Kamil")
                .last_name("Memmedov")
                .username("admin1_username")
                .password(passwordEncoder.encode("adminPass"))
                .email("kamilmmmdov2905@gmail.com")
                .activated(true)
                .build();

        AdminEntity admin2 = AdminEntity.builder()
                .first_name("Admin2")
                .last_name("Admin2Last")
                .username("admin2_username")
                .password(passwordEncoder.encode("adminPass "))
                .email("elnarzulfuqarli2001@gmail.com")
                .activated(true)
                .build();

        // Set admin roles
        Set<Role> roles = utilService.getAdminRoles();
        admin1.setRoles(roles);
        admin2.setRoles(roles);

        adminRepository.save(admin1);
        adminRepository.save(admin2);
    }


    public void insertCompany() {
        // Insert 1 company
        Role companyRole = roleRepository.findByName("COMPANY");
        if (companyRole == null) {
            throw new CompanyNotFoundException("Company role not found.");
        }

        CompanyEntity company1 = CompanyEntity.builder()
                .first_name("Kenan")
                .last_name("Memmedov")
                .email("string@gmail.com")
                .company_name("Company")
                .job_title("CEO")
                .message("I want to build company")
                .activated(true)
                .password(passwordEncoder.encode("string"))
                .build();

        company1.setRoles(Collections.singleton(companyRole));
        companyRepository.save(company1);
    }

    public void insertProgram() {
        // Create a new BugBountyProgramWithAssetTypePayload instance
        ProgramPayload programPayload = new ProgramPayload();

        // Set data into the payload
        programPayload.setFromDate(LocalDate.of(2024, 4, 15));
        programPayload.setToDate(LocalDate.of(2024, 5, 15));
        programPayload.setPolicy("Responsible Disclosure Policy");
        programPayload.setNotes("Bug Bounty program for ExampleCompany's web assets.");


        // Create and set prohibits payloads
        ProhibitPayload prohibitPayload = new ProhibitPayload();
        prohibitPayload.setProhibitAdded("Prohibits the use of automated scanners without prior permission.");
        programPayload.setProhibits(Arrays.asList(prohibitPayload));

        CompanyEntity company = companyRepository.findByEmail("string@gmail.com");

        ProgramAssetPayload programAssetPayload = new ProgramAssetPayload();
        BaseProgramAssetPayload lowProgramAsset = new BaseProgramAssetPayload();


        Set<AssetPayload> assets = new HashSet<>();
        AssetPayload asset = new AssetPayload();
        asset.setType("domain");
        asset.setPrice(33.0);
        Set<String> assetNames = new HashSet<>(Set.of("x.com", "y.com"));
        asset.setNames(assetNames);
        assets.add(asset);


        lowProgramAsset.setAssets(assets);


        // Create and set payloads for Medium, High, and Critical assets
        BaseProgramAssetPayload mediumProgramAssetPayload = new BaseProgramAssetPayload();

        BaseProgramAssetPayload highProgramAssetPayload = new BaseProgramAssetPayload();

        BaseProgramAssetPayload criticalProgramAssetPayload = new BaseProgramAssetPayload();

        // Populate asset sets for Medium, High, and Critical assets
        Set<AssetPayload> highAssets = new HashSet<>();
        Set<AssetPayload> criticalAssets = new HashSet<>();

        // Populate asset sets
        AssetPayload mediumAsset = new AssetPayload();
        mediumAsset.setType("domain");
        mediumAsset.setPrice(68.0);
        mediumAsset.setNames(new HashSet<>(Arrays.asList("z.com", "w.com"))); // Example asset names for Medium asset

        AssetPayload mediumAsset2 = new AssetPayload();
        mediumAsset2.setType("mobile");
        mediumAsset2.setPrice(85.5);
        mediumAsset2.setNames(new HashSet<>(Arrays.asList("mob1", "mob2"))); // Example asset names for Medium asset

        Set<AssetPayload> mediumAssets = new HashSet<>(Set.of(mediumAsset,mediumAsset2)); // add 2 assets to set of assets for test

        AssetPayload highAsset = new AssetPayload();
        highAsset.setType("domain");
        highAsset.setPrice(149.8);
        highAsset.setNames(new HashSet<>(Arrays.asList("p.com", "q.com"))); // Example asset names for High asset
        highAssets.add(highAsset);

        AssetPayload criticalAsset = new AssetPayload();
        criticalAsset.setType("domain");
        criticalAsset.setPrice(560.0);
        criticalAsset.setNames(new HashSet<>(Arrays.asList("m.com", "n.com"))); // Example asset names for Critical asset
        criticalAssets.add(criticalAsset);

        // Set asset sets to their respective payloads
        mediumProgramAssetPayload.setAssets(mediumAssets);
        highProgramAssetPayload.setAssets(highAssets);
        criticalProgramAssetPayload.setAssets(criticalAssets);

        // Set payloads to the main program asset payload
        programAssetPayload.setLowAsset(lowProgramAsset);
        programAssetPayload.setMediumAsset(mediumProgramAssetPayload);
        programAssetPayload.setHighAsset(highProgramAssetPayload);
        programAssetPayload.setCriticalAsset(criticalProgramAssetPayload);

        programPayload.setAsset(programAssetPayload);

        programService.createBugBountyProgramForTest(programPayload,company);
    }
}
