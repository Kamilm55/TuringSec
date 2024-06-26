package com.turingSecApp.turingSec.model.entities.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;


import jakarta.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(exclude = "bugBountyReport")
@ToString(exclude = "bugBountyReport")
@Entity
@Table(name = "bug_bounty_collaborators")
public class CollaboratorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bug_bounty_report_id")
    @JsonIgnore
    private Report bugBountyReport;

    @Column(name = "hacker_username")
    private String hackerUsername;

    @Column(name = "collaboration_percentage")
    private Double collaborationPercentage;

    // Constructors, getters, and setters

}
