//package com.turingSecApp.turingSec.util;
//
//import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
//import com.turingSecApp.turingSec.dao.repository.UserRepository;
//import com.turingSecApp.turingSec.exception.custom.UnauthorizedException;
//import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//@RequiredArgsConstructor
//public class Util {
//    private final UserRepository userRepository;
//    // Method to retrieve authenticated user
//    public static UserEntity getAuthenticatedUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated()) {
//            String username = authentication.getName();
//            return userRepository.findByUsername(username)
//                    .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));
//        } else {
//            throw new UnauthorizedException();
//        }
//    }
//}
