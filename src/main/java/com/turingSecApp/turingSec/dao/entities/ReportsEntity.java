package com.turingSecApp.turingSec.dao.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(exclude = {"user", "bugBountyProgram", "collaborators"})
@ToString(exclude = {"user", "bugBountyProgram", "collaborators"})
@Table(name = "bug_bounty_reports")
public class ReportsEntity {
    @Id
   // @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asset")
    private String asset;

    @Column(name = "weakness")
    private String weakness;

    @Column(name = "severity")
    private String severity;

    @Column(name = "method_name")
    private String methodName;


    @Column(name = "proof_of_concept")
    private String proofOfConcept;

    @Column(name = "discovery_details")
    private String discoveryDetails;

    @Column(name = "last_activity")
    private Date lastActivity;

    @Column(name = "report_title")
    private String reportTitle;

    @Column(name = "rewards_status")
    private String rewardsStatus;

    @Column(name = "vulnerability_url")
    private String vulnerabilityUrl;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private UserEntity user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bug_bounty_program_id")
    @JsonIgnore
    private BugBountyProgramEntity bugBountyProgram;

    @Column(name = "own_percentage")
    private Double ownPercentage = 100.0; // Default own percentage is 100%

    @OneToMany(mappedBy = "bugBountyReport", cascade = CascadeType.ALL)
    private List<CollaboratorEntity> collaborators = new ArrayList<>();

    public void addCollaborator(CollaboratorEntity collaborator) {
        collaborators.add(collaborator);
        collaborator.setBugBountyReport(this);
    }

}
