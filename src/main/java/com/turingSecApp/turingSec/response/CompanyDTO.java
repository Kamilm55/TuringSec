package com.turingSecApp.turingSec.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.turingSecApp.turingSec.dao.entities.BugBountyProgramEntity;
import com.turingSecApp.turingSec.dao.entities.role.Role;
import com.turingSecApp.turingSec.dao.entities.role.UserRoles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDTO {
    private Long id;
    private String first_name;
    private String last_name;
    private String email;
    private String company_name;
    private String job_title;
    private String assets;
    private Set<Long> bugBountyPrograms_id;

}