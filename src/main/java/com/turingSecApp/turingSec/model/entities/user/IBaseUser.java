package com.turingSecApp.turingSec.model.entities.user;

import com.turingSecApp.turingSec.model.entities.role.Role;

import java.util.Set;

public interface IBaseUser {
    Long getId();
    String getFirst_name();
    String getLast_name();
    String getEmail();
    boolean isActivated();
    Set<Role> getRoles();
}

