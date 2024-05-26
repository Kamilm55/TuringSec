package com.turingSecApp.turingSec.response.company;

import com.turingSecApp.turingSec.dao.entities.program.Program;
import com.turingSecApp.turingSec.dao.entities.role.UserRoles;
import lombok.Data;

import java.util.Set;
@Data
public class CompanyResponse {
    private Long id;
    private String first_name;
    private String last_name;
    private String email;
    private String company_name;
    private String job_title;
    private String assets;
    private String message;

    private boolean approved; // Indicates whether the company registration is approved

    //private Set<Role> roles;

    private Set<Program> bugBountyPrograms;

    private Set<UserRoles> userRoles;

    private Long fileId;
}
