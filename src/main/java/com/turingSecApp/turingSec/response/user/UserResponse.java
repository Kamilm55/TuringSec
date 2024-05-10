package com.turingSecApp.turingSec.response.user;

import com.turingSecApp.turingSec.dao.entities.role.Role;
import com.turingSecApp.turingSec.response.user.HackerResponse;

import java.util.Set;

public class UserResponse {
    private Long id;
    private String first_name;
    private String last_name;
    private String country;
    private String username;
    private String password;
    private String email;
    private String activationToken;
    private boolean activated;
    private Set<Role> roles;
    private HackerResponse hackerResponse;
}