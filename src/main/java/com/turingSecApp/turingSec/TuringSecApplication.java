package com.turingSecApp.turingSec;

import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.model.entities.program.Program;
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
import com.turingSecApp.turingSec.service.ProgramsService;
import com.turingSecApp.turingSec.service.interfaces.IHackerService;
import com.turingSecApp.turingSec.service.interfaces.IUserService;
import com.turingSecApp.turingSec.util.MockData;
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
    private final ProgramsService programsService;
    private final UtilService utilService;
    public static void main(String[] args) {
        SpringApplication.run(TuringSecApplication.class, args);
    }

    @Override
    @Transactional
    public void run(String... args){
        insertMockData(); // for h2 db -> in development environment


        UserEntity hacker1 = userRepository.findByEmail("kamilmmmdov2905@gmail.com");
        if(hacker1!=null) {
            System.out.println("hacker roles: " + hacker1.getRoles().toString());
        }
        System.out.println(hacker1.getHacker());

        CompanyEntity company = companyRepository.findByEmail("string@gmail.com");
        if(company!=null){
            System.out.println("company roles: "+company.getRoles().toString());
        }

        Optional<AdminEntity> admin1 = adminRepository.findByUsername("admin1_username");
        admin1.ifPresent(adminEntity -> System.out.println("admin roles: " + adminEntity.getRoles().toString()));
    }

    private void insertMockData() {
        setHackerRoles();
        insertHackerForDefaultUsers();

//        setAdminRoles(); // todo: it is not working , change role structure
        insertProgram();
    }

    private void setHackerRoles() {
        UserEntity user1 = userRepository.findByUsername("Username").orElseThrow(()->new UserNotFoundException("Admin not found with username:Username"));
        Set<Role> hackerRoles = utilService.getHackerRoles();
        user1.setRoles(hackerRoles);
        userRepository.save(user1);

        UserEntity user2 = userRepository.findByUsername("Hacker_2").orElseThrow(()->new UserNotFoundException("Admin not found with username:Hacker_2"));
        user2.setRoles(hackerRoles);

        userRepository.save(user2);
    }

    private void insertHackerForDefaultUsers() {
        UserEntity user1 = userRepository.findByUsername("Username").orElseThrow(()->new UserNotFoundException("Admin not found with username:Username"));
        UserEntity user2 = userRepository.findByUsername("Hacker_2").orElseThrow(()->new UserNotFoundException("Admin not found with username:Hacker_2"));

        if (user1.getHacker() == null){
                createHackerSetToUserAndSave(user1);
                createHackerSetToUserAndSave(user2);
        }
    }

    private void createHackerSetToUserAndSave(UserEntity user2) {
        HackerEntity hackerEntity2 = new HackerEntity();
        hackerEntity2.setUser(user2);
        hackerEntity2.setFirst_name(user2.getFirst_name());
        hackerEntity2.setLast_name(user2.getLast_name());
        hackerEntity2.setCountry(user2.getCountry());
        hackerRepository.save(hackerEntity2);

        user2.setHacker(hackerEntity2);
        userRepository.save(user2);
    }

    private void setAdminRoles() {
        Set<Role> adminRoles = utilService.getAdminRoles();

        AdminEntity admin1 = adminRepository.findByUsername("admin1_username").orElseThrow(()->new UserNotFoundException("Admin not found with username:admin1_username"));
        admin1.setRoles(adminRoles);


        AdminEntity admin2 = adminRepository.findByUsername("admin2_username").orElseThrow(()->new UserNotFoundException("Admin not found with username:admin2_username"));
        admin2.setRoles(adminRoles);

        adminRepository.save(admin1);
        adminRepository.save(admin2);

    }

    private void insertProgram()  {
        // Check if exists? if exists don't insert it violates non-unique condition
        if (!MockData.mockDataNames.contains("Program1")) {
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

            programsService.createBugBountyProgramForTest(programPayload,company);
        }


    }


}
