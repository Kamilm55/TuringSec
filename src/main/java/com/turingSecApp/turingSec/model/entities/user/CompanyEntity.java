package com.turingSecApp.turingSec.model.entities.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.model.entities.role.Role;
import com.turingSecApp.turingSec.model.entities.role.UserRoles;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(exclude = {"roles", "bugBountyPrograms", "userRoles"})
@ToString(exclude = {"roles", "bugBountyPrograms", "userRoles"})
@Builder
@Table(name = "companies")
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String first_name;
    private String last_name;
    private String email;
    private String company_name;
    private String job_title;
    private String assets;//
    private String message;
    private String password;

    private boolean approved; // Indicates whether the company registration is approved

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "company_role",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @JsonIgnore
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Program> bugBountyPrograms;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<UserRoles> userRoles = new HashSet<>();;

    public void removeProgram(Long programId) {
        if (bugBountyPrograms != null) {
            // If cascading deletion is needed, it will be handled based on the CascadeType.ALL
            // defined in the association
            bugBountyPrograms.removeIf(program -> program.getId().equals(programId));
        }
    }

}