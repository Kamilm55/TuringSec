package com.turingSecApp.turingSec.model.entities.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.turingSecApp.turingSec.model.entities.program.Program;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(exclude = {"roles", "bugBountyPrograms", "userRoles"},callSuper = true)
@ToString(exclude = {"roles", "bugBountyPrograms", "userRoles"},callSuper = true)
@SuperBuilder
@DiscriminatorValue("COMPANY")
@Table(name = "companies")
public class CompanyEntity extends BaseUser {

    private String message;
    private String company_name;
    private String job_title;
    private boolean has_background_pic;
    private boolean has_profile_pic;
    private String bio;
    private String linkedin;
    private String twitter;
    private String assets;//no need for this field

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonIgnore
    private Set<Program> bugBountyPrograms;

    public void removeProgram(Long programId) {
        if (bugBountyPrograms != null) {
            // If cascading deletion is needed, it will be handled based on the CascadeType.ALL
            // defined in the association
            bugBountyPrograms.removeIf(program -> program.getId().equals(programId));
        }
    }
}