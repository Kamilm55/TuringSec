package com.turingSecApp.turingSec;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.NameBasedGenerator;
import com.turingSecApp.turingSec.model.entities.user.AdminEntity;
import com.turingSecApp.turingSec.model.entities.user.IBaseUser;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.model.repository.*;
import com.turingSecApp.turingSec.model.repository.report.ReportsRepository;
import com.turingSecApp.turingSec.service.program.ProgramService;
import com.turingSecApp.turingSec.service.interfaces.IHackerService;
import com.turingSecApp.turingSec.service.interfaces.IMockDataService;
import com.turingSecApp.turingSec.service.interfaces.IUserService;
import com.turingSecApp.turingSec.util.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
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
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReportsRepository reportsRepository;
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
        String name = "globalUserID";
        UUID uuid = generator.generate(name);
        System.out.println("Generated UUIDv5 for global user id: " + uuid.toString());

    }

}
