package com.turingSecApp.turingSec.service.user;

import com.turingSecApp.turingSec.dao.entities.AdminEntity;
import com.turingSecApp.turingSec.dao.entities.CompanyEntity;
import com.turingSecApp.turingSec.dao.entities.role.Role;
import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
import com.turingSecApp.turingSec.dao.repository.AdminRepository;
import com.turingSecApp.turingSec.dao.repository.CompanyRepository;
import com.turingSecApp.turingSec.dao.repository.RoleRepository;
import com.turingSecApp.turingSec.dao.repository.UserRepository;
import com.turingSecApp.turingSec.exception.custom.CompanyNotFoundException;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.service.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        System.out.println("(loadUserByUsername) -> username: " + username);
        System.out.println("(loadUserByUsername) -> user: " +user);
        if (user != null) {
            return new CustomUserDetails(user);
        }

        AdminEntity admin = adminRepository.findByUsername(username).orElse(null);
        if (admin != null) {
                // Set the "ADMIN" role for the admin
                Role adminRole = roleRepository.findByName("ADMIN");
                if (adminRole == null) {
                    throw new NotFoundException("Admin role not found.");
                }

                admin.setRoles(Collections.singleton(adminRole));

            return new CustomUserDetails(admin);
        }

        CompanyEntity companyEntity = companyRepository.findByEmail(username);
        if (companyEntity != null) {
            return new CustomUserDetails(companyEntity);
        }else {
            throw new CompanyNotFoundException("Company does not found in UserDetailsServiceImpl.");
        }


    }
}
