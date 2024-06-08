package com.turingSecApp.turingSec;

import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.model.entities.role.Role;
import com.turingSecApp.turingSec.model.entities.user.AdminEntity;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.entities.user.HackerEntity;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.model.repository.*;
import com.turingSecApp.turingSec.model.repository.program.ProgramsRepository;
import com.turingSecApp.turingSec.payload.program.*;
import com.turingSecApp.turingSec.payload.program.asset.AssetPayload;
import com.turingSecApp.turingSec.payload.program.asset.BaseProgramAssetPayload;
import com.turingSecApp.turingSec.payload.program.asset.ProgramAssetPayload;
import com.turingSecApp.turingSec.service.ProgramService;
import com.turingSecApp.turingSec.service.interfaces.IHackerService;
import com.turingSecApp.turingSec.service.interfaces.IMockDataService;
import com.turingSecApp.turingSec.service.interfaces.IUserService;
import com.turingSecApp.turingSec.util.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
//
//        insertMockData(); // for h2 db -> in development environment

        //todo: insert all mock data without liquibase


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
    }

    private void insertMockData() {

//        insertRoles();
//        insertUsers(); // from github
//        insertAdmins();
//        insertCompany();
//        insertProgram();

//        setHackerRoles();
//        insertHackerForDefaultUsers();

//        setAdminRoles(); // todo: it is not working , change role structure
//        insertProgram();
    }



    private void insertProgram()  {


            // Create a new BugBountyProgramWithAssetTypePayload instance
            ProgramPayload programPayload = new ProgramPayload();

            // Set data into the payload
            programPayload.setFromDate(LocalDate.of(2024, 4, 15));
            programPayload.setToDate(LocalDate.of(2024, 5, 15));
            programPayload.setPolicy("Responsible Disclosure Policy");
            programPayload.setNotes("Bug Bounty program for ExampleCompany's web assets.");


            // Create and set prohibits payloads
            StrictPayload strictPayload = new StrictPayload();
            strictPayload.setProhibitAdded("Prohibits the use of automated scanners without prior permission.");
            programPayload.setProhibits(Arrays.asList(strictPayload));

            CompanyEntity company = companyRepository.findByEmail("string@gmail.com");

            ProgramAssetPayload programAssetPayload = new ProgramAssetPayload();
            BaseProgramAssetPayload lowProgramAsset = new BaseProgramAssetPayload();
            lowProgramAsset.setPrice(45.0);

            Set<AssetPayload> assets = new HashSet<>();
            AssetPayload asset = new AssetPayload();
            asset.setType("domain");
            Set<String> assetNames = new HashSet<>(Set.of("x.com", "y.com"));
            asset.setNames(assetNames);
            assets.add(asset);


            lowProgramAsset.setAssets(assets);


            // Create and set payloads for Medium, High, and Critical assets
            BaseProgramAssetPayload mediumProgramAssetPayload = new BaseProgramAssetPayload();
            mediumProgramAssetPayload.setPrice(55.0); // Set price for Medium asset

            BaseProgramAssetPayload highProgramAssetPayload = new BaseProgramAssetPayload();
            highProgramAssetPayload.setPrice(65.0); // Set price for High asset

            BaseProgramAssetPayload criticalProgramAssetPayload = new BaseProgramAssetPayload();
            criticalProgramAssetPayload.setPrice(75.0); // Set price for Critical asset

            // Populate asset sets for Medium, High, and Critical assets
            Set<AssetPayload> highAssets = new HashSet<>();
            Set<AssetPayload> criticalAssets = new HashSet<>();

            // Populate asset sets
            AssetPayload mediumAsset = new AssetPayload();
            mediumAsset.setType("domain");
            mediumAsset.setNames(new HashSet<>(Arrays.asList("z.com", "w.com"))); // Example asset names for Medium asset

            AssetPayload mediumAsset2 = new AssetPayload();
            mediumAsset2.setType("mobile");
            mediumAsset2.setNames(new HashSet<>(Arrays.asList("mob1", "mob2"))); // Example asset names for Medium asset

            Set<AssetPayload> mediumAssets = new HashSet<>(Set.of(mediumAsset,mediumAsset2)); // add 2 assets to set of assets for test

            AssetPayload highAsset = new AssetPayload();
            highAsset.setType("domain");
            highAsset.setNames(new HashSet<>(Arrays.asList("p.com", "q.com"))); // Example asset names for High asset
            highAssets.add(highAsset);

            AssetPayload criticalAsset = new AssetPayload();
            criticalAsset.setType("domain");
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
