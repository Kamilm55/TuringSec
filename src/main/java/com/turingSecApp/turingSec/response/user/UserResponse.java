package com.turingSecApp.turingSec.response.user;


import com.turingSecApp.turingSec.model.enums.Role;

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