package com.turingSecApp.turingSec.util;

import com.turingSecApp.turingSec.dao.entities.CompanyEntity;
import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
import com.turingSecApp.turingSec.dao.repository.CompanyRepository;
import com.turingSecApp.turingSec.dao.repository.UserRepository;
import com.turingSecApp.turingSec.exception.custom.UnauthorizedException;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UtilService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    // Method to retrieve authenticated user(Hacker)
    public UserEntity getAuthenticatedHacker() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));
        } else {
            throw new UnauthorizedException();
        }
    }

    // Method to retrieve authenticated company
    public CompanyEntity getAuthenticatedCompany() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            CompanyEntity company = companyRepository.findByEmail(email);
            if(company==null){
                throw  new UserNotFoundException("Company with email " + email + " not found");
            }
            return company;
        } else {
            throw new UnauthorizedException();
        }
    }
}
