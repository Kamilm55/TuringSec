package com.turingSecApp.turingSec;

import com.turingSecApp.turingSec.model.entities.user.AdminEntity;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.model.repository.*;
import com.turingSecApp.turingSec.model.repository.program.ProgramRepository;
import com.turingSecApp.turingSec.service.program.ProgramService;
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
    private final ProgramRepository programRepository;
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


}
