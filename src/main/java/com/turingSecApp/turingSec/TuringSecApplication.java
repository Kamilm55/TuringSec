package com.turingSecApp.turingSec;

import com.turingSecApp.turingSec.dao.entities.AdminEntity;
import com.turingSecApp.turingSec.dao.entities.HackerEntity;
import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
import com.turingSecApp.turingSec.dao.repository.AdminRepository;
import com.turingSecApp.turingSec.dao.repository.HackerRepository;
import com.turingSecApp.turingSec.dao.repository.UserRepository;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.service.HackerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@ComponentScan(basePackages = {"com.turingSecApp.turingSec", "com.turingSecApp.turingSec.config"})
@RequiredArgsConstructor
public class TuringSecApplication implements CommandLineRunner {
    private final HackerService hackerService;
    private final HackerRepository hackerRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    public static void main(String[] args) {
        SpringApplication.run(TuringSecApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        System.out.println(hackerService.findById(18L));
//       // System.out.println(hackerService.findById(10L));
//        System.out.println(hackerService.findById(33L));

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
                .password(passwordEncoder.encode("password"))
                .email("kamilmmmdov2905@gmail.com")
                .build();

        AdminEntity admin2 = AdminEntity.builder()
                .first_name("Admin2")
                .last_name("Admin2Last")
                .username("admin2_username")
                .password(passwordEncoder.encode("admin2_password"))
                .email("admin2@example.com")
                .build();

        //todo: admin must be user create fk and insert
        adminRepository.save(admin1);
        adminRepository.save(admin2);


    }
}
