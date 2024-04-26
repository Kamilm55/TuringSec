package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.dao.entities.AdminEntity;
import com.turingSecApp.turingSec.dao.entities.CompanyEntity;
import com.turingSecApp.turingSec.dao.entities.role.Role;
import com.turingSecApp.turingSec.dao.repository.AdminRepository;
import com.turingSecApp.turingSec.dao.repository.RoleRepository;
import com.turingSecApp.turingSec.exception.custom.EmailAlreadyExistsException;
import com.turingSecApp.turingSec.exception.custom.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailNotificationService emailNotificationService;

//    public ResponseEntity<?> registerAdmin(AdminEntity admin) {
//        // You may want to perform validation or other checks before saving the admin
//        // Ensure the user doesn't exist
//        if (adminRepository.findByUsername(admin.getUsername()) != null) {
//            throw new UserAlreadyExistsException("Username is already taken.");
//        }
//
//        if (adminRepository.findByEmail(admin.getEmail()) != null) {
//            throw new EmailAlreadyExistsException("Email is already taken.");
//
//        }
//
//        // Set the "ADMIN" role for the admin
//        Role adminRole = roleRepository.findByName("ADMIN");
//        if (adminRole == null) {
//            throw new NotFoundException("Admin role not found.");
//        }
//
//        admin.setRoles(Collections.singleton(adminRole));
//        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
//
//
//         adminRepository.save(admin);
//
//        return ResponseEntity.ok(admin);
//    }

    // Other admin management methods
    public void notifyCompanyForApproval(CompanyEntity company , String pass) {

        // Compose the email message
        String subject = "Company Registration Approval from TuringSec";
        String content = "Congratulations! We accepted your company request.\n\n"
                + "Please login to the app with this generated password: " + pass ;

//        System.out.println(company.getEmail());
//        System.out.println(company);
        // Send email notification to the company
        emailNotificationService.sendEmail(company.getEmail(), subject, content);
//        System.out.println(company.getEmail().equals("kamilmdov2905@gmail.com"));
//        emailNotificationService.sendEmail("kamilmdov2905@gmail.com", "subject", "content");
    }

}
