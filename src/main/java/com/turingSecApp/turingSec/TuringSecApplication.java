package com.turingSecApp.turingSec;

import com.turingSecApp.turingSec.model.entities.user.AdminEntity;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.model.repository.*;
import com.turingSecApp.turingSec.model.repository.report.ReportRepository;
import com.turingSecApp.turingSec.service.interfaces.IHackerService;
import com.turingSecApp.turingSec.service.interfaces.IMockDataService;
import com.turingSecApp.turingSec.service.interfaces.IUserService;
import com.turingSecApp.turingSec.service.program.ProgramService;
import com.turingSecApp.turingSec.util.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
@ComponentScan(basePackages = {"com.turingSecApp.turingSec", "com.turingSecApp.turingSec.config"})
@RequiredArgsConstructor
@Slf4j
public class TuringSecApplication implements CommandLineRunner {
    private final IHackerService hackerService;
    private final IUserService userService;
    private final HackerRepository hackerRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private  final IMockDataService mockDataService;
    public static void main(String[] args) {
        SpringApplication.run(TuringSecApplication.class, args);
    }

    @Override
    @Transactional
    public void run(String... args){
        mockDataService.insertMockData();

        UserEntity hacker1 = userRepository.findByEmail("mockhacker1@gmail.com");
        UserEntity hacker2 = userRepository.findByEmail("mockhacker2@gmail.com");

        CompanyEntity company = companyRepository.findByEmail("string@gmail.com");

        AdminEntity admin1 = adminRepository.findByUsername("admin1_username").get();
        AdminEntity admin2 = adminRepository.findByUsername("admin2_username").get();

        System.out.println("------------------------- All Mock Data (USERS) ----------------------------");
        System.out.println(
                hacker1 + "\n" + hacker2 + "\n" + company + "\n" + admin1 + "\n" + admin2
        );
        System.out.println("Password for hackers: userPass");
        System.out.println("Password for companies: companyPass");
        System.out.println("Password for admins: adminPass");

        System.out.println("----------------------------------------------------------------------------");

    }

}
