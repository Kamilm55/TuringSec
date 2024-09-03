package com.turingSecApp.turingSec.response.company;

import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.model.enums.Role;
import lombok.Data;

import java.util.Set;

@Data
public class CompanyResponse {
    private String id;
    private String first_name;
    private String last_name;
    private String email;
    private String company_name;
    private String job_title;
    private String assets;
    private String message;

    private boolean activated; // Indicates whether the company registration is approved

    private Set<Role> roles;

    private Set<Program> bugBountyPrograms;


    private Long fileId;
}